package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private Session currentSession;
    private final ConnectionManager connections = new ConnectionManager();
    private MySqlDataAccess dataAccess;

    public WebSocketHandler() {
        try {
            this.dataAccess = new MySqlDataAccess();
        } catch (Exception e) {
            System.out.println("Unable to connect to the database");
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        this.currentSession = session;

        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        AuthData authData = authenticate(session, userGameCommand.getAuthToken());
        if (authData == null) {
            sendErrorMessage(session, "Not authorized");
            return;
        }

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            ConnectCommand connectCommand = new Gson().fromJson(message, ConnectCommand.class);
            connect(authData.username(), connectCommand, session);
        } else if (userGameCommand.getCommandType() == UserGameCommand.CommandType.LEAVE) {
            LeaveCommand leaveCommand = new Gson().fromJson(message, LeaveCommand.class);
            leave(authData.username(), leaveCommand);
        } else if (userGameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
            makeMove(authData.username(), session, makeMoveCommand);
        } else if (userGameCommand.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
            resign(authData.username(), resignCommand);
        }
    }

    public void connect(String username, ConnectCommand connectCommand, Session session) {
        GameData gameData = null;
        try {
            gameData = dataAccess.getGame(connectCommand.getGameID());
        } catch (DataAccessException e) {
            unableToConnectToDB(session);
            return;
        }

        if (gameData == null) {
            sendErrorMessage(session, "Invalid Game ID");
            return;
        }

        connections.add(username, gameData.gameID(), session);

        String joinMessage = username + " joined as ";
        if (Objects.equals(gameData.whiteUsername(), username)) {
            joinMessage += "WHITE";
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            joinMessage += "BLACK";
        } else {
            joinMessage += "an observer";
        }

        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
        sendJSON(new Gson().toJson(loadGameMessage));
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, joinMessage);
        try {
            connections.notifyAllOthersInGame(username, gameData.gameID(), notificationMessage);
        } catch (IOException e) {
            System.out.println("Unable to send connection messages");
        }
    }

    public void makeMove(String username, Session currentSession, MakeMoveCommand makeMoveCommand) {
        GameData gameData;
        try {
            gameData = dataAccess.getGame(makeMoveCommand.getGameID());
        } catch (DataAccessException e) {
            unableToConnectToDB(currentSession);
            return;
        }

        if (connections.isGameEnd(gameData.gameID())) {
            sendErrorMessage(currentSession, "The game has ended");
            return;
        }

        if (isObserver(username, gameData)) {
            sendErrorMessage(currentSession, "Observers can't make moves");
            return;
        }

        if (getCurrentColor(username, gameData) != gameData.game().getTeamTurn()) {
            sendErrorMessage(currentSession, "It is not your turn");
            return;
        }

        ChessGame newChessGame;
        try {
            newChessGame = gameData.game().makeMove(makeMoveCommand.getChessMove());
        } catch (InvalidMoveException e) {
            sendErrorMessage(currentSession, "Invalid Move");
            return;
        }

        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), newChessGame);

        try {
            dataAccess.updateGame(newGameData);
        } catch (DataAccessException e) {
            unableToConnectToDB(currentSession);
        }

        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, newGameData);
        try {
            connections.loadGameAll(loadGameMessage);
        } catch (IOException e) {
            System.out.println("Unable to send load game messages");
        }

        if (isGameInCheckMate(newGameData)) {
            if (newGameData.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
                String checkmateMsg = newGameData.whiteUsername() + " is in Checkmate. " + newGameData.blackUsername() + " has Won!";
                NotificationMessage checkmateNotificationMsg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkmateMsg);
                try {
                    connections.notifyAllInGame(newGameData.gameID(), checkmateNotificationMsg);
                } catch (IOException e) {
                    System.out.println("Unable to send checkmate messages");
                }
            } else {
                String checkmateMsg = newGameData.blackUsername() + " is in Checkmate. " + newGameData.whiteUsername() + " has Won!";
                NotificationMessage checkmateNotificationMsg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkmateMsg);
                try {
                    connections.notifyAllInGame(newGameData.gameID(), checkmateNotificationMsg);
                } catch (IOException e) {
                    System.out.println("Unable to send checkmate messages");
                }
            }
            connections.endGame(newGameData.gameID());
            return;
        }

        if (isGameInStalemate(newGameData)) {
            String stalemateMsg = "Game is in stalemate";
            NotificationMessage stalemateNotificationMsg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, stalemateMsg);
            try {
                connections.notifyAllInGame(newGameData.gameID(), stalemateNotificationMsg);
            } catch (IOException e) {
                System.out.println("Unable to send stalemate messages");
            }
            connections.endGame(newGameData.gameID());
            return;
        }

        notifyCheck(newChessGame, newGameData);
    }

    private void leave(String username, LeaveCommand leaveCommand) {
        GameData gameData;
        try {
            gameData = dataAccess.getGame(leaveCommand.getGameID());
        } catch (DataAccessException e) {
            unableToConnectToDB(currentSession);
            return;
        }

        GameData newGameData = gameData;
        if (Objects.equals(username, gameData.whiteUsername())) {
            newGameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else if (Objects.equals(username, gameData.blackUsername())) {
            newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
        }

        try {
            dataAccess.updateGame(newGameData);
        } catch (DataAccessException e) {
            unableToConnectToDB(currentSession);
        }

        String leaveMsg = username + " left the game";
        NotificationMessage leaveNotificationMsg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, leaveMsg);
        try {
            connections.notifyAllOthersInGame(username, newGameData.gameID(), leaveNotificationMsg);
        } catch (IOException e) {
            System.out.println("Unable to send leave game messages");
        }
    }

    private void resign(String username, ResignCommand resignCommand) {
        connections.endGame(resignCommand.getGameID());

        String resignMsg = username + " has resigned";
        NotificationMessage resignNotificationMsg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, resignMsg);
        try {
            connections.notifyAllInGame(resignCommand.getGameID(), resignNotificationMsg);
        } catch (IOException e) {
            System.out.println("Unable to send resign game messages");
        }
    }

    private boolean isGameInCheckMate(GameData gameData) {
        if (gameData.game().isInCheck(ChessGame.TeamColor.WHITE) || gameData.game().isInCheck(ChessGame.TeamColor.BLACK)) {
            return true;
        }
        return false;
    }

    private boolean isGameInStalemate(GameData gameData) {
        if (gameData.game().isInStalemate(ChessGame.TeamColor.WHITE) || gameData.game().isInStalemate(ChessGame.TeamColor.BLACK)) {
            return true;
        }
        return false;
    }

    private void notifyCheck(ChessGame newChessGame, GameData gameData) {
        if (newChessGame.isInCheck(ChessGame.TeamColor.WHITE)) {
            String checkMessage = gameData.whiteUsername() + " is in check";
            NotificationMessage checkNotificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMessage);
            try {
                connections.notifyAllInGame(gameData.gameID(), checkNotificationMessage);
            } catch (IOException e) {
                System.out.println("Unable to send check messages");
            }
        } else if (newChessGame.isInCheck(ChessGame.TeamColor.BLACK)) {
            String checkMessage = gameData.blackUsername() + " is in check";
            NotificationMessage checkNotificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMessage);
            try {
                connections.notifyAllInGame(gameData.gameID(), checkNotificationMessage);
            } catch (IOException e) {
                System.out.println("Unable to send check messages");
            }
        }
    }

    private ChessGame.TeamColor getCurrentColor(String username, GameData gameData) {
        ChessGame.TeamColor teamColor;
        if (Objects.equals(gameData.whiteUsername(), username)) {
            teamColor = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            teamColor = ChessGame.TeamColor.BLACK;
        } else {
            teamColor = null;
        }
        return teamColor;
    }

    private boolean isObserver(String username, GameData gameData) {
        return !Objects.equals(gameData.whiteUsername(), username) && !Objects.equals(gameData.blackUsername(), username);
    }

    private AuthData authenticate(Session currentSession, String authToken) {
        AuthData authData = null;
        try {
            authData = dataAccess.getAuth(authToken);
        } catch (DataAccessException e) {
            unableToConnectToDB(currentSession);
        }

        return authData;
    }

    private void sendJSON(String json) {
        try {
            this.currentSession.getRemote().sendString(json);
        } catch (IOException e) {
            System.out.println("Unable to send message");
        }
    }

    private void sendNotificationMessage(Session currentSession, String message) {
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        try {
            currentSession.getRemote().sendString(new Gson().toJson((notificationMessage)));
        } catch (IOException e) {
            System.out.println("Unable to send message");
        }
    }

    private void sendErrorMessage(Session currentSession, String message) {
        ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
        try {
            currentSession.getRemote().sendString(new Gson().toJson((errorMessage)));
        } catch (IOException e) {
            System.out.println("Unable to send message");
        }
    }

    private void unableToConnectToDB(Session currentSession) {
        String errorMessage = "Unable to connect to the database";
        System.out.println("Message Received but " + errorMessage);
        try {
            currentSession.getRemote().sendString(new Gson().toJson(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, errorMessage)));
        } catch (IOException e) {
            System.out.println("Unable to send message");
        }
    }
}
