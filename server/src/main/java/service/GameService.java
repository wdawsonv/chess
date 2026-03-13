package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;

public class GameService {
    private static final GameDAO GAME_DAO = new MemoryGameDAO();

    public static void clear() {
        GAME_DAO.clearGameDB();
    }
}
