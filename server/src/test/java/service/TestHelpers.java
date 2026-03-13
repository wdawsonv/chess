package service;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import model.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;

public class TestHelpers {

    public static void setup() throws AlreadyTakenException, BadRequestException {
        UserService.clear();
        AuthService.clear();
        GameService.clear();
        UserService.register(new RegisterRequest("username1", "password1", "email1"));
        UserService.register(new RegisterRequest("username2", "password2", "email2"));
        UserService.register(new RegisterRequest("username3", "password3", "email3"));

    }
}
