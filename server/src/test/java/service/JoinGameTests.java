package service;

import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class JoinGameTests {

    @BeforeEach
    void setup() throws BadRequestException, AlreadyTakenException, DataAccessException {
        TestHelpers.gameSetup();
    }

    @Test
    @DisplayName("Join Game Positive Test")
    public void joinGamesPositiveTest() throws AlreadyTakenException, BadRequestException {
        GameService.createGame(new CreateGameRequest("gameName"));
        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);

        GameService.joinGame(joinGameRequest, "username1");
        ArrayList<GameData> answer = new ArrayList<>();
        answer.add(new GameData(1, "username1", null, "gameName", new ChessGame()));
        Assertions.assertEquals(GameService.listGames(), answer);
    }

    @Test
    @DisplayName("Join Games Negative Unauthorized Test")
    public void joinGamesNegativeUnauthorizedTest() {
        Assertions.assertThrows(UnauthorizedException.class, () -> AuthService.findAuth("blah"));
    }
}
