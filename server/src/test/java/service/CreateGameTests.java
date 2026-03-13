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
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        UserService.register(new RegisterRequest("username1", "password1", "email1"));
        RegisterResult loginResult = AuthService.createAuth("username1");
        String authToken = loginResult.authToken();
    }

    @Test
    @DisplayName("Create Game Positive Test")
    public void createGamePositiveTest() {
        Assertions.assertEquals(GameService.createGame(new CreateGameRequest("gameName")), new CreateGameResult(0000));
    }

    @Test
    @DisplayName("Create Game Negative Bad Request Test")
    public void createGameNegativeBadRequestTest() {
        Assertions.assertThrows(BadRequestException.class, () -> GameService.createGame(new CreateGameRequest(null)));
    }
}
