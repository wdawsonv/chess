package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ServiceClearTests {
/*
    @Test
    @DisplayName("Clear Database")
    public void clearDatabase() {
        UserService.clear();
        GameService.clear();
        AuthService.clear();
        Assertions.assertAll(
                "All services should be clear",
                () -> Assertions.assertNull(MemoryUserDAO.users),
                () -> Assertions.assertNull(MemoryGameDAO.games),
                () -> Assertions.assertNull(MemoryAuthDAO.auths)
        ); //this needs to be changes once we implement getting info back yay

    }

 */
    @Test
    @DisplayName("Add User Positive Test")
    public void addUserPositive() {
        UserData userData = new UserData("username", "password", "email");
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        UserService.register(registerRequest);
        Assertions.assertTrue(UserService.getUser("username"));
    }
}
