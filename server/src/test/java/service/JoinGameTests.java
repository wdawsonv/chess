package service;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import model.CreateGameRequest;
import model.CreateGameResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class JoinGameTests {

    @BeforeEach
    void setup() throws BadRequestException, AlreadyTakenException {
        TestHelpers.GameSetup();
    }

    @Test
    @DisplayName("Join Game Positive Test")
    public void joinGamesPositiveTest() throws AlreadyTakenException, BadRequestException {
        Assertions.assertTrue(new ArrayList<>(), GameService.listGames()); //something man UNFINISHED HERE
    }

    @Test
    @DisplayName("Join Games Negative Unauthorized Test")
    public void joinGamesNegativeUnauthorizedTest() {
        Assertions.assertThrows(UnauthorizedException.class, () -> AuthService.findAuth("blah"));
    }
}
