package client.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
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
                    ServerMessage serverMsg = new Gson().fromJson(message, ServerMessage.class);
                    messageHandler.serverMsgRouter(serverMsg);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void connect(String authToken, int gameID) throws ResponseException {
        try {
            ConnectCommand connectCommand = new ConnectCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(connectCommand));
            this.session.close();
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
