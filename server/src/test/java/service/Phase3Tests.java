package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import model.User;
import dataaccess.MemoryDataAccess;
import service.UserService;
import service.Phase3TestHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Phase3Tests {

    static final Phase3TestHelper helper = new Phase3TestHelper();
    static final UserService userService = new UserService(new MemoryDataAccess());

    @BeforeEach
    void clearAll() throws DataAccessException {
        userService.deleteUsers();
    }

    @Test
    void registerNewUserPositive() throws DataAccessException {
        var user1 = new User("username1", "email1", "password1");
        var user2 = new User("username2", "email2", "password2");
        var user3 = new User("username3", "email3", "password3");
        try {
            userService.register(user1);
        } catch (AlreadyTakenException e) {}
        try {
            userService.register(user2);
        } catch (AlreadyTakenException e) {}
        try {
            userService.register(user3);
        } catch (AlreadyTakenException e) {}

        HashMap<User, String> users = userService.listUsers();

        HashMap<User, String> expectedUsers = new HashMap<>();
        expectedUsers.put(user1, "1");
        expectedUsers.put(user2, "2");
        expectedUsers.put(user3, "3");

        boolean identical = false;
        for (User user : users.keySet()) {
            String mainUsername = user.username();
            identical = false;

            for (User expectedUser : expectedUsers.keySet()) {
                String secondaryUsername = expectedUser.username();

                if (mainUsername.equals(secondaryUsername)) {
                    identical = true;
                    break;
                }
            }
            if (!identical) {
                break;
            }
        }
        assert helper.messyIdenticalUsers(users, expectedUsers);
    }

    @Test
    void registerNewUserNegative() throws DataAccessException {
        var user1 = new User("username1", "email1", "password1");
        var user2 = new User("username2", "email2", "password2");
        var user3 = new User("username3", "email3", "password3");
        try {
            userService.register(user1);
        } catch (AlreadyTakenException e) {}
        try {
            userService.register(user1);
        } catch (AlreadyTakenException e) {}
        try {
            userService.register(user3);
        } catch (AlreadyTakenException e) {}

        HashMap<User, String> users = userService.listUsers();

        HashMap<User, String> expectedUsers = new HashMap<>();
        expectedUsers.put(user1, "1");
        expectedUsers.put(user3, "2");

        assert (helper.messyIdenticalUsers(users, expectedUsers) && helper.noRepeatedUsernames(users, expectedUsers));
    }

}
