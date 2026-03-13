package service;

import dataaccess.*;
import model.LoginRequest;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginTests {

    @BeforeEach
    void setup() throws AlreadyTakenException, BadRequestException {
        TestHelpers.setup();
    }

    @Test
    @DisplayName("Login Positive Test")
    public void loginPositive() throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        Assertions.assertTrue(UserService.checkLogin(new LoginRequest("username1", "password1")));
    }

    @Test
    @DisplayName("Login Negative Bad Request Test")
    public void loginNegativeBadRequestTest() {
        Assertions.assertThrows(BadRequestException.class, () ->
                UserService.checkLogin(new LoginRequest(null, "password2")));
    }

    @Test
    @DisplayName("Login Negative Unauthorized Test")
    public void loginNegativeUnauthorizedTest() {
        Assertions.assertThrows(UnauthorizedException.class, () ->
                UserService.checkLogin(new LoginRequest("username4", "password1")));
    }
}
