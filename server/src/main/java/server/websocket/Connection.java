package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    private final int gameID;
    private final Session session;

    public Connection(int gameID, Session session) {
        this.gameID = gameID;
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public int getGameID() {
        return gameID;
    }

    public void send(String msg) throws IOException {
        this.session.getRemote().sendString(msg);
    }
}
