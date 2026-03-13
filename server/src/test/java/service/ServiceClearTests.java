package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ServiceClearTests {

    @Test
    @DisplayName("Clear Database")
    public void clearDatabase() {
        UserService.clear();
        GameService.clear();
        AuthService.clear();
        Assertions.assertFalse(UserService.getUser("username1"));
        //this needs to be changes once we implement getting info back yay

    }



}
