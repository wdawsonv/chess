package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class DatabaseGameDAO implements GameDAO {
    @Override
    public void clearGameDB() {

    }

    @Override
    public boolean verifyGameExists(String gameName) {
        return false;
    }

    @Override
    public GameData getGame(Integer gameID) {
        return null;
    }

    @Override
    public Integer createGame(String gameName) {
        return 0;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public void addPlayer(Integer gameID, ChessGame.TeamColor teamColor, String username) {

    }
}
