package service;

import dataaccess.*;
import model.*;

public class GameService {
    private static final GameDAO GAME_DAO = new MemoryGameDAO();

    public static void clear() {
        GAME_DAO.clearGameDB();
    }

    public static CreateGameResult createGame(CreateGameRequest createGameRequest) throws AlreadyTakenException, BadRequestException {
        String gameName = createGameRequest.gameName();
        if (gameName == null) {
            throw new BadRequestException("bad request");
        } else if (GAME_DAO.getGame(gameName)) {
            throw new AlreadyTakenException("game name already taken");
        } else {
            int gameID = GAME_DAO.createGame(gameName);
            return new CreateGameResult(gameID);
        }
    }
}
