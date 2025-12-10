package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void add(Session session) {
        connections.put(session, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(String message) throws IOException {
        for (Session session : connections.values()) {
            if (session.isOpen()) {
                session.getRemote().sendString(message);
            }
        }
    }
}
