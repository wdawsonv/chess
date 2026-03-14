package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LogoutTests {

    @BeforeEach
    void setup() throws AlreadyTakenException, BadRequestException, DataAccessException {
        TestHelpers.userSetup();
    }

    @Test
    @DisplayName("Logout Positive Test")
    public void logoutPositiveTest() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterResult loginResult = AuthService.createAuth("username1");
        String authToken = loginResult.authToken();
        Assertions.assertDoesNotThrow(() -> AuthService.logout(new LogoutRequest(authToken)));
    }

    @Test
    @DisplayName("Logout Negative Unauthorized Test")
    public void logoutNegativeUnauthorizedTest() {
        Assertions.assertThrows(UnauthorizedException.class, () ->
                AuthService.logout(new LogoutRequest("not an authtoken")));
    }
}
