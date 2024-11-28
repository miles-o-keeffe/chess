package server.websocket;

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

@WebSocket
public class WebSocketHandler {
    private Session currentSession;
    private final ConnectionManager connections = new ConnectionManager();
    private MySqlDataAccess dataAccess;

    public WebSocketHandler() {
        try {
            this.dataAccess = new MySqlDataAccess();
        } catch (Exception e) {
            unableToConnectToDB();
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        this.currentSession = session;

        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        AuthData authData = authenticate(userGameCommand.getAuthToken());
        if (authData == null) {
            sendErrorMessage("Not authorized");
            return;
        }

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            ConnectCommand connectCommand = new Gson().fromJson(message, ConnectCommand.class);
            connect(connectCommand);
        } else if (userGameCommand.getCommandType() == UserGameCommand.CommandType.LEAVE) {
            LeaveCommand leaveCommand = new Gson().fromJson(message, LeaveCommand.class);
        } else if (userGameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
        } else if (userGameCommand.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
        }
    }

    public void loadGame() {

    }

    public void connect(ConnectCommand connectCommand) {
        GameData gameData = null;
        try {
            gameData = dataAccess.getGame(connectCommand.getGameID());
        } catch (DataAccessException e) {
            unableToConnectToDB();
            return;
        }

        if (gameData == null) {
            sendErrorMessage("Invalid Game ID");
            return;
        }

        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
        sendJSON(new Gson().toJson(loadGameMessage));
    }

    public AuthData authenticate(String authToken) {
        AuthData authData = null;
        try {
            authData = dataAccess.getAuth(authToken);
        } catch (DataAccessException e) {
            unableToConnectToDB();
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

    public void sendNotificationMessage(String message) {
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        try {
            this.currentSession.getRemote().sendString(new Gson().toJson((notificationMessage)));
        } catch (IOException e) {
            System.out.println("Unable to send message");
        }
    }

    public void sendErrorMessage(String message) {
        ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
        try {
            this.currentSession.getRemote().sendString(new Gson().toJson((errorMessage)));
        } catch (IOException e) {
            System.out.println("Unable to send message");
        }
    }

    public void unableToConnectToDB() {
        String errorMessage = "Unable to connect to the database";
        System.out.println("Message Received but " + errorMessage);
        try {
            this.currentSession.getRemote().sendString(new Gson().toJson(new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, errorMessage)));
        } catch (IOException e) {
            System.out.println("Unable to send message");
        }
    }
}
