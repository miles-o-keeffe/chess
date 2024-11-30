package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    MessageHandler messageHandler;

    public WebSocketFacade(String serverURL, MessageHandler messageHandler) throws ResponseException {
        try {
            serverURL = serverURL.replace("http", "ws");
            URI socketURI = new URI(serverURL + "/ws");
            this.messageHandler = messageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new javax.websocket.MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    try {
                        ServerMessage serverMsg = new Gson().fromJson(message, ServerMessage.class);
                        if (serverMsg.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                            messageHandler.notificationMessageNotify(new Gson().fromJson(message, NotificationMessage.class));
                        } else if (serverMsg.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                            messageHandler.loadGameMessageNotify(new Gson().fromJson(message, LoadGameMessage.class));
                        } else if (serverMsg.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                            messageHandler.errorMessageNotify(new Gson().fromJson(message, ErrorMessage.class));
                        }
                    } catch (Exception e) {
                        System.out.println("Unable to receive server message");
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void connect(String authToken, int gameID) throws ResponseException {
        ConnectCommand connectCommand = new ConnectCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.send(connectCommand);
    }

    public void makeMove(String authToken, int gameID, ChessMove chessMove) throws ResponseException {
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(authToken, gameID, chessMove);
        this.send(makeMoveCommand);
    }

    public void leave(String authToken, int gameID) throws ResponseException {
        LeaveCommand leaveCommand = new LeaveCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        this.send(leaveCommand);
    }

    public void resign(String authToken, int gameID) throws ResponseException {
        ResignCommand resignCommand = new ResignCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        this.send(resignCommand);
    }

    public void send(UserGameCommand command) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
