package service;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ListGamesTests {

    @BeforeEach
    void setup() throws BadRequestException, AlreadyTakenException, DataAccessException {
        TestHelpers.gameSetup();
    }

    @Test
    @DisplayName("List Games Positive Test")
    public void listGamesPositiveTest() throws AlreadyTakenException, BadRequestException, DataAccessException {
        Assertions.assertEquals(new ArrayList<>(), GameService.listGames());
    }

    @Test
    @DisplayName("List Games Negative Unauthorized Test")
    public void listGamesNegativeUnauthorizedTest() {
        Assertions.assertThrows(UnauthorizedException.class, () -> AuthService.findAuth("blah"));
    }
}
