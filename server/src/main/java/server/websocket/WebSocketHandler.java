package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import exception.ResponseException;
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
        } else if (userGameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
            makeMove(authData.username(), session, makeMoveCommand);
        } else if (userGameCommand.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
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
            connections.notifyAllInGame(username, gameData.gameID(), notificationMessage);
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
    }

    public ChessGame.TeamColor getCurrentColor(String username, GameData gameData) {
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

    public boolean isObserver(String username, GameData gameData) {
        return !Objects.equals(gameData.whiteUsername(), username) && !Objects.equals(gameData.blackUsername(), username);
    }

    public AuthData authenticate(Session currentSession, String authToken) {
        AuthData authData = null;
        try {
            authData = dataAccess.getAuth(authToken);
        } catch (DataAccessException e) {
            unableToConnectToDB(currentSession);
        }

        return authData;
    }

    public void sendJSON(String json) {
        try {
            this.currentSession.getRemote().sendString(json);
        } catch (IOException e) {
            System.out.println("Unable to send message");
        }
    }

    public void sendNotificationMessage(Session currentSession, String message) {
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        try {
            currentSession.getRemote().sendString(new Gson().toJson((notificationMessage)));
        } catch (IOException e) {
            System.out.println("Unable to send message");
        }
    }

    public void sendErrorMessage(Session currentSession, String message) {
        ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
        try {
            currentSession.getRemote().sendString(new Gson().toJson((errorMessage)));
        } catch (IOException e) {
            System.out.println("Unable to send message");
        }
    }

    public void unableToConnectToDB(Session currentSession) {
        String errorMessage = "Unable to connect to the database";
        System.out.println("Message Received but " + errorMessage);
        try {
            currentSession.getRemote().sendString(new Gson().toJson(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, errorMessage)));
        } catch (IOException e) {
            System.out.println("Unable to send message");
        }
    }
}
