package service;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import model.RegisterRequest;
import model.RegisterResult;

public class TestHelpers {

    public static void UserSetup() throws AlreadyTakenException, BadRequestException {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        UserService.register(new RegisterRequest("username1", "password1", "email1"));
        UserService.register(new RegisterRequest("username2", "password2", "email2"));
        UserService.register(new RegisterRequest("username3", "password3", "email3"));

    }

    public static void GameSetup() throws BadRequestException, AlreadyTakenException {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        UserService.register(new RegisterRequest("username1", "password1", "email1"));
    }
}
