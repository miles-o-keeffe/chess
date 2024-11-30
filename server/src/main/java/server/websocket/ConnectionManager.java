package server.websocket;

import com.google.gson.Gson;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<String, Connection>();

    public void add(String visitorName, int gameID, org.eclipse.jetty.websocket.api.Session session) {
        connections.put(visitorName, new Connection(gameID, session));
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void notifyAllOthersInGame(String excludeUserName, int gameID, NotificationMessage notification) throws IOException {
        for (var entry : connections.entrySet()) {
            if ((!Objects.equals(entry.getKey(), excludeUserName) && entry.getValue().getGameID() == gameID)) {
                entry.getValue().send(new Gson().toJson(notification));
            }
        }
    }

    public void notifyAllInGame(int gameID, NotificationMessage notification) throws IOException {
        for (var entry : connections.entrySet()) {
            if (entry.getValue().getGameID() == gameID) {
                entry.getValue().send(new Gson().toJson(notification));
            }
        }
    }

    public void loadGameAll(LoadGameMessage loadGameMessage) throws IOException {
        for (var entry : connections.entrySet()) {
            if (entry.getValue().getGameID() == loadGameMessage.getGame().gameID()) {
                entry.getValue().send(new Gson().toJson(loadGameMessage));
            }
        }
    }

    public void endGame(int gameID) {
        for (var entry : connections.entrySet()) {
            if (entry.getValue().getGameID() == gameID) {
                entry.getValue().setGameOver(true);
            }
        }
    }

    public boolean isGameEnd(int gameID) {
        for (var entry : connections.entrySet()) {
            if (entry.getValue().getGameID() == gameID) {
                return entry.getValue().isGameOver();
            }
        }
        return false;
    }

}
