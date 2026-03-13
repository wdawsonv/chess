package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private static ArrayList<GameData> games = new ArrayList<>();
    private int recentID = 0;

    @Override
    public void clearGameDB() {
        games.clear();
    }

    @Override
    public boolean getGame(String gameName) {
        for (GameData game : games) {
            if (gameName.equals(game.gameName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int createGame(String gameName) {
        recentID++;
        games.add(new GameData(recentID, null, null, gameName, new ChessGame()));
        return recentID;
    }

}
