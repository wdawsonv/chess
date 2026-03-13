package dataaccess;

import model.*;

import java.util.ArrayList;

public interface GameDAO {
    void clearGameDB();

    boolean getGame(String gameName);

    int createGame(String gameName);

    ArrayList<GameData> listGames();
}