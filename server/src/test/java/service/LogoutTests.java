package service;

import dataaccess.*;
import model.LoginRequest;
import model.RegisterRequest;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LogoutTests {

    @BeforeEach
    void setup() throws AlreadyTakenException, BadRequestException {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        UserService.register(new RegisterRequest("username1", "password1", "email1"));
        UserService.register(new RegisterRequest("username2", "password2", "email2"));
        UserService.register(new RegisterRequest("username3", "password3", "email3"));

    }


    @Test
    @DisplayName("Logout Positive Test")
    public void LogoutPositive() throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        RegisterResult loginResult = AuthService.createAuth("username1");
        String authToken = loginResult.authToken();
        Assertions.assertDoesNotThrow(() -> AuthService.logout(new LogoutRequest(authToken)));
    }

    @Test
    @DisplayName("Logout Negative Unauthorized Test")
    public void LogoutNegativeUnauthorizedTest() {
        Assertions.assertThrows(UnauthorizedException.class, () ->
                AuthService.logout(new LogoutRequest("not an authtoken")));
    }
}
