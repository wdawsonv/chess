package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CreateGameTests {

    @BeforeEach
    void setup() throws BadRequestException, AlreadyTakenException {
        TestHelpers.GameSetup();
    }

    @Test
    @DisplayName("Create Game Positive Test")
    public void createGamePositiveTest() throws AlreadyTakenException, BadRequestException {
        Assertions.assertEquals(new CreateGameResult(1), GameService.createGame(new CreateGameRequest("gameName")));
    }

    @Test
    @DisplayName("Create Game Negative Bad Request Test")
    public void createGameNegativeBadRequestTest() {
        Assertions.assertThrows(BadRequestException.class, () -> GameService.createGame(new CreateGameRequest(null)));
    }
}
