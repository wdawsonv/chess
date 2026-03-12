package service;

import dataaccess.GameDAO;

public class GameService {
    public static void clear() {
        GameDAO.clearGameDB();
    }
}
