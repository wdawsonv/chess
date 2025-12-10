package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final Map<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();

    //reverse lookup for the love of the game (and also to be able to log out)
    private final Map<Session, Integer> sessionToGame = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        if (!gameSessions.containsKey(gameID)) {
            gameSessions.put(gameID, ConcurrentHashMap.newKeySet());
        }
        gameSessions.get(gameID).add(session);
        sessionToGame.put(session, gameID);
    }

    public void remove(Session session) {
        Integer gameID = sessionToGame.remove(session);
        if (gameID == null) {
            return;
        }

        if (gameSessions.containsKey(gameID)) {
            gameSessions.get(gameID).remove(session);
        }
    }

    public void broadcastExcept(int gameID, Session excludedSession, String message) {
        if (!gameSessions.containsKey(gameID)) return;
        for (Session session : gameSessions.get(gameID)) {
            if (session != excludedSession && session.isOpen()) {
                session.getRemote().sendString(message, null);
            }
        }
    }

    public void broadcast(int gameID, String message) throws IOException {
        if (!gameSessions.containsKey(gameID)) return;
        for (Session session : gameSessions.get(gameID)) {
            if (session.isOpen()) {
                session.getRemote().sendString(message);
            }
        }
    }
}
