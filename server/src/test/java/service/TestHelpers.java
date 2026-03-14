package service;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import model.RegisterRequest;

public class TestHelpers {

    public static void userSetup() throws AlreadyTakenException, BadRequestException, DataAccessException {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        UserService.register(new RegisterRequest("username1", "password1", "email1"));
        UserService.register(new RegisterRequest("username2", "password2", "email2"));
        UserService.register(new RegisterRequest("username3", "password3", "email3"));

    }

    public static void gameSetup() throws BadRequestException, AlreadyTakenException, DataAccessException {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        UserService.register(new RegisterRequest("username1", "password1", "email1"));
        UserService.register(new RegisterRequest("username2", "password2", "email2"));
    }
}
