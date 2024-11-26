package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import exception.ResponseException;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private Session currentSession;
    private final ConnectionManager connections = new ConnectionManager();
    private MySqlDataAccess dataAccess;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        this.currentSession = session;

        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        AuthData authData = authenticate(userGameCommand.getAuthToken());
        if (authData == null) {
            return;
        }

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            ConnectCommand connectCommand = new Gson().fromJson(message, ConnectCommand.class);
        } else if (userGameCommand.getCommandType() == UserGameCommand.CommandType.LEAVE) {
            LeaveCommand leaveCommand = new Gson().fromJson(message, LeaveCommand.class);
        } else if (userGameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
        } else if (userGameCommand.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
        }

        try {
            this.dataAccess = new MySqlDataAccess();
        } catch (Exception e) {
            unableToConnectToDB();
        }
    }

    public void loadGame() {

    }

    public void connect(ConnectCommand connectCommand) {

    }

    public AuthData authenticate(String authToken) {
        AuthData authData = null;
        try {
            authData = dataAccess.getAuth(authToken);
        } catch (DataAccessException e) {
            unableToConnectToDB();
        }

        if (authData == null) {
            sendNotificationMessage("Not authorized");
        }

        return authData;
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

    public void sendNotificationMessage(String message) {
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        try {
            this.currentSession.getRemote().sendString(new Gson().toJson((notificationMessage)));
        } catch (IOException e) {
            System.out.println("Unable to send message");
        }
    }
}
