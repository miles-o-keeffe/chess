package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
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

    public void notifyAllInGame(String excludeVisitorName, int gameID, NotificationMessage notification) throws IOException {
        for (var entry : connections.entrySet()) {
            if ((!Objects.equals(entry.getKey(), excludeVisitorName) && entry.getValue().getGameID() == gameID)) {
                entry.getValue().send(new Gson().toJson(notification));
            }
        }
    }

}
