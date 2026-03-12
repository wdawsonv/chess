package dataaccess;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private static ArrayList<GameData> games = new ArrayList<>();

    static public void clearGameDB() {
        games.clear();
    }

}
