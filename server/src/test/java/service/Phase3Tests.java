package service;

import dataaccess.DataAccessException;
import model.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.UserData;
import dataaccess.MemoryDataAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Phase3Tests {

    static final UserService userService = new UserService(new MemoryDataAccess());

    @BeforeEach
    void clearAll() throws DataAccessException {
        userService.deleteUsers();
    }

    @Test
    void registerNewUserPositive() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        var user2 = new UserData("username2", "email2", "password2");
        var user3 = new UserData("username3", "email3", "password3");
        try {
            userService.register(user1);
        } catch (AlreadyTakenException e) {}
        try {
            userService.register(user2);
        } catch (AlreadyTakenException e) {}
        try {
            userService.register(user3);
        } catch (AlreadyTakenException e) {}

        List<UserData> users = userService.listUsers();

        List<UserData> expectedUsers = new ArrayList<>();
        expectedUsers.add(user1);
        expectedUsers.add(user2);
        expectedUsers.add(user3);

        assert (expectedUsers.equals(users));
    }

    @Test
    void registerNewUserNegative() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        var user2 = new UserData("username2", "email2", "password2");
        var user3 = new UserData("username3", "email3", "password3");
        try {
            userService.register(user1);
        } catch (AlreadyTakenException e) {}
        try {
            userService.register(user1);
        } catch (AlreadyTakenException e) {}
        try {
            userService.register(user3);
        } catch (AlreadyTakenException e) {}

        List<UserData> users = userService.listUsers();

        List<UserData> expectedUsers = new ArrayList<>();
        expectedUsers.add(user1);
        expectedUsers.add(user3);

        assert (expectedUsers.equals(users));
    }

//    @Test
//    void loginPositive() throws DataAccessException {
//        var user1 = new UserData("username1", "email1", "password1");
//        try {
//            userService.register(user1);
//        } catch (AlreadyTakenException e) {}
//
//        try {
//            userService.login(new LoginRequest(user1.username(), user1.password()));
//        } catch (BadPasswordException e) {}
//
//        //hold up gotta make logout stuff first :PPPPP
//    }

}
