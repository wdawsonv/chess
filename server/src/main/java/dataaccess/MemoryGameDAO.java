package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO {
    private static ArrayList<GameData> games = new ArrayList<>();
    private int recentID = 0;

    @Override
    public void clearGameDB() {
        games.clear();
    }

    @Override
    public boolean verifyGameExists(String gameName) {
        for (GameData game : games) {
            if (gameName.equals(game.gameName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public GameData getGame(Integer gameID) {
        for (GameData game : games) {
            if (Objects.equals(gameID, game.gameID())) {
                return game;
            }
        }
        return null;
    }

    @Override
    public Integer createGame(String gameName) {
        recentID++;
        games.add(new GameData(recentID, null, null, gameName, new ChessGame()));
        return recentID;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return games;
    }

    @Override
    public void addPlayer(Integer gameID, ChessGame.TeamColor teamColor, String username) {
        for (int i = 0; i < games.size(); i++) {
            if (Objects.equals(gameID, games.get(i).gameID())) {
                if (teamColor == ChessGame.TeamColor.WHITE) {
                    games.set(i, new GameData(games.get(i).gameID(), username, games.get(i).blackUsername(), games.get(i).gameName(), games.get(i).game()));
                } else {
                    games.set(i, new GameData(games.get(i).gameID(), games.get(i).whiteUsername(), username, games.get(i).gameName(), games.get(i).game()));
                }
            }
        }
    }

}
