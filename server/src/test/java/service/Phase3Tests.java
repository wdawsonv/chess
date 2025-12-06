package service;

import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.MemoryDataAccess;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class Phase3Tests {

    static final MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
    static final UserService userService = new UserService(memoryDataAccess);


    @BeforeEach
    void clearAll() throws DataAccessException {
        userService.deleteUsers();
    }

    @Test
    void registerNewUserPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        var user2 = new UserData("username2", "email2", "password2");
        var user3 = new UserData("username3", "email3", "password3");
        try {
            userService.register(user1);
        } catch (AlreadyTakenException | BadPasswordException _) {}
        try {
            userService.register(user2);
        } catch (AlreadyTakenException _) {} catch (BadPasswordException e) {
            throw new RuntimeException(e);
        }
        try {
            userService.register(user3);
        } catch (AlreadyTakenException | BadPasswordException _) {}

        List<UserData> users = userService.listUsers();

        List<UserData> expectedUsers = new ArrayList<>();
        expectedUsers.add(user1);
        expectedUsers.add(user2);
        expectedUsers.add(user3);

        assert (expectedUsers.equals(users));
    }

    @Test
    void registerNewUserNegativeTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        var user3 = new UserData("username3", "email3", "password3");
        try {
            userService.register(user1);
        } catch (AlreadyTakenException | BadPasswordException _) {}
        try {
            userService.register(user1);
        } catch (AlreadyTakenException | BadPasswordException _) {}
        try {
            userService.register(user3);
        } catch (AlreadyTakenException | BadPasswordException _) {}

        List<UserData> users = userService.listUsers();

        List<UserData> expectedUsers = new ArrayList<>();
        expectedUsers.add(user1);
        expectedUsers.add(user3);

        assert (expectedUsers.equals(users));
    }

    @Test
    void logoutPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = userService.register(user1);
            userService.logout(register1.authToken());

            assert (memoryDataAccess.getAuth(register1.authToken()) == null);
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException _) {}


    }

    @Test
    void logoutNegativeTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        try {
            userService.register(user1);
            assertThrows(UnauthorizedException.class, () -> userService.logout("not an auth token :PPPPPP"));

        } catch (AlreadyTakenException | BadPasswordException _) {}


    }
    @Test
    void loginPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        try {
            RegisterResult register1 = userService.register(user1);
            userService.logout(register1.authToken());
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException _) {}

        try {
            LoginResult loginfo = userService.login(new LoginRequest(user1.username(), user1.password()));
            assert (memoryDataAccess.getAuth(loginfo.authToken()) != null);
        } catch (BadPasswordException | MissingUsernameException | BadRequestException _) {}
    }

    @Test
    void loginNegativeTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        try {
            RegisterResult register1 = userService.register(user1);
            userService.logout(register1.authToken());
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException _) {}


        assertThrows(MissingUsernameException.class, () -> userService.login(new LoginRequest("incorrect username", user1.password())));
    }

    @Test
    void createGamePositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = userService.register(user1);
            CreateResult result1 = userService.createGame("gamename1", register1.authToken());
            CreateResult result2 = userService.createGame("gamename2", register1.authToken());

            assert(result1.gameID() == 1001 && result2.gameID() == 1002);
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException | BadRequestException _) {}
    }

    @Test
    void createGameNegativeTest() throws DataAccessException, AlreadyTakenException{
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = userService.register(user1);
            userService.createGame("gamename4", register1.authToken());
            assertThrows(AlreadyTakenException.class, () -> userService.createGame("gamename4", register1.authToken()));
        } catch (UnauthorizedException | BadPasswordException | BadRequestException _) {}
    }

    @Test
    void listGamesPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = userService.register(user1);
            userService.createGame("gamename1", register1.authToken());
            userService.createGame("gamename2", register1.authToken());

            assert(userService.listGames(register1.authToken()).size() == 2);
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException | BadRequestException _) {}
    }

    @Test
    void listGamesNegativeTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = userService.register(user1);
            userService.createGame("gamename1", register1.authToken());
            userService.createGame("gamename2", register1.authToken());
            assertThrows(UnauthorizedException.class, () -> userService.listGames("bad token"));
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException | BadRequestException _) {}
    }

    @Test
    void clearDataPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = userService.register(user1);
            userService.createGame("gamename1", register1.authToken());
            userService.createGame("gamename2", register1.authToken());

            userService.clearAllData();

            var user2 = new UserData("username1", "email1", "password1");
            RegisterResult register2 = userService.register(user2);
            assert(userService.listGames(register2.authToken()).isEmpty());
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException | BadRequestException _) {}
    }

    @Test
    void addUserWhitePositiveTest() {

        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = userService.register(user1);
            userService.createGame("gamename1", register1.authToken());
            userService.joinGame(1001, "WHITE", register1.authToken());

            var user2 = new UserData("username1", "email1", "password1");
            userService.register(user2);

            //get games (first game) should have username1 on white team
            assert(memoryDataAccess.getGamesList().getFirst().whiteUsername().equals("username1"));
        } catch (AlreadyTakenException | UnauthorizedException | DataAccessException | BadPasswordException | BadRequestException _) {}
    }
    @Test
    void addUserWhiteNegativeTest() {

        //another test
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = userService.register(user1);
            userService.createGame("gamename1", register1.authToken());
            userService.joinGame(1001, "WHITE", register1.authToken());

            var user2 = new UserData("username1", "email1", "password1");
            RegisterResult register2 = userService.register(user2);

            assertThrows(AlreadyTakenException.class, () -> userService.joinGame(1001, "WHITE", register2.authToken()));

        } catch (AlreadyTakenException | UnauthorizedException | DataAccessException | BadPasswordException | BadRequestException _) {}
    }
}
