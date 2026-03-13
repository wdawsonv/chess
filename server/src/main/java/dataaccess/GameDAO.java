package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.ArrayList;

public interface GameDAO {
    void clearGameDB();

    boolean verifyGameExists(String gameName);

    GameData getGame(Integer gameID);

    Integer createGame(String gameName);

    ArrayList<GameData> listGames();

    void addPlayer(Integer gameID, ChessGame.TeamColor teamColor, String username);
}