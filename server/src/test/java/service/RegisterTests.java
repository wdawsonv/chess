package service;

import dataaccess.*;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegisterTests {

    @BeforeEach
    void setup() throws AlreadyTakenException, BadRequestException, DataAccessException {
        TestHelpers.userSetup();
    }

    @Test
    @DisplayName("Add User Positive Test")
    public void addUserPositiveTest() throws BadRequestException, AlreadyTakenException, DataAccessException {
        UserData userData = new UserData("username", "password", "email");
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        UserService.register(registerRequest);
        Assertions.assertTrue(UserService.getUser("username"));
    }

    @Test
    @DisplayName("Add User Negative Already Taken Test")
    public void addUserNegativeAlreadyTakenTest() {
        Assertions.assertThrows(AlreadyTakenException.class, () ->
                UserService.register(new RegisterRequest("username1", "password", "email")));
    }

    @Test
    @DisplayName("Add User Negative Bad Request Test")
    public void addUserNegativeBadRequestTest() {
        Assertions.assertThrows(BadRequestException.class, () ->
                UserService.register(new RegisterRequest(null, "password", "email")));
    }
}
