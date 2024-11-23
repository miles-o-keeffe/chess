package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public final ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<String, Session>();

    public void add(String visitorName, org.eclipse.jetty.websocket.api.Session session) {
        connections.put(visitorName, session);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

}
