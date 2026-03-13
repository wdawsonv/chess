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
    void setup() throws AlreadyTakenException {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        UserService.register(new RegisterRequest("username1", "password1", "email1"));
        UserService.register(new RegisterRequest("username2", "password2", "email2"));
        UserService.register(new RegisterRequest("username3", "password3", "email3"));

    }


    @Test
    @DisplayName("Add User Positive Test")
    public void addUserPositive() throws AlreadyTakenException {
        UserData userData = new UserData("username", "password", "email");
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        UserService.register(registerRequest);
        Assertions.assertTrue(UserService.getUser("username"));
    }

    @Test
    @DisplayName("Add User Negative Test")
    public void addUserNegative() throws AlreadyTakenException {
        Assertions.assertFalse(UserService.register(new RegisterRequest("username1", "password", "email")));
    }
}
