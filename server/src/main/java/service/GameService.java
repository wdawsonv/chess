package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.ArrayList;

public class GameService {
    private static final GameDAO GAME_DAO;

    static {
        try {
            GAME_DAO = new DatabaseGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clear() throws DataAccessException {
        GAME_DAO.clearGameDB();
    }

    public static CreateGameResult createGame(CreateGameRequest createGameRequest)
            throws AlreadyTakenException, BadRequestException, DataAccessException {

        String gameName = createGameRequest.gameName();
        if (gameName == null) {
            throw new BadRequestException("bad request");
        } else if (GAME_DAO.verifyGameExists(gameName)) {
            throw new AlreadyTakenException("game name already taken");
        } else {
            int gameID = GAME_DAO.createGame(gameName);
            return new CreateGameResult(gameID);
        }
    }

    public static ArrayList<GameData> listGames() throws DataAccessException {
        return GAME_DAO.listGames();
    }

    public static void joinGame(JoinGameRequest joinGameRequest, String username)
            throws BadRequestException, AlreadyTakenException, DataAccessException {

        Integer gameID = joinGameRequest.gameID();
        ChessGame.TeamColor teamColor = joinGameRequest.playerColor();

        if (gameID == null || teamColor == null || GAME_DAO.getGame(gameID) == null) {
            throw new BadRequestException("bad request");
        } else {
            GameData gameData = GAME_DAO.getGame(gameID);
            if ((teamColor == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null)
                    || (teamColor == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null)) {
                throw new AlreadyTakenException("already taken");
            }

            GAME_DAO.addPlayer(gameID, teamColor, username);
        }
    }
}
