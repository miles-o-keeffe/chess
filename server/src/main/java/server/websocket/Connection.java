package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public int gameID;
    public Session session;

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
        session.getRemote().sendString(msg);
    }
}
