package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.ArrayList;

public interface GameDAO {
    void clearGameDB() throws DataAccessException;

    boolean verifyGameExists(String gameName) throws DataAccessException;

    GameData getGame(Integer gameID) throws DataAccessException;

    Integer createGame(String gameName) throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    void addPlayer(Integer gameID, ChessGame.TeamColor teamColor, String username) throws DataAccessException;
}