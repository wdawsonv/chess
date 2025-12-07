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

    static final MemoryDataAccess MemoryDataAccess = new MemoryDataAccess();
    static final UserService UserService = new UserService(MemoryDataAccess);


    @BeforeEach
    void clearAll() throws DataAccessException {
        UserService.deleteUsers();
    }

    @Test
    void registerNewUserPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        var user2 = new UserData("username2", "email2", "password2");
        var user3 = new UserData("username3", "email3", "password3");
        try {
            UserService.register(user1);
        } catch (AlreadyTakenException | BadPasswordException e) {}
        try {
            UserService.register(user2);
        } catch (AlreadyTakenException e) {} catch (BadPasswordException e) {
            throw new RuntimeException(e);
        }
        try {
            UserService.register(user3);
        } catch (AlreadyTakenException | BadPasswordException e) {}

        List<UserData> users = UserService.listUsers();

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
            UserService.register(user1);
        } catch (AlreadyTakenException | BadPasswordException e) {}
        try {
            UserService.register(user1);
        } catch (AlreadyTakenException | BadPasswordException e) {}
        try {
            UserService.register(user3);
        } catch (AlreadyTakenException | BadPasswordException e) {}

        List<UserData> users = UserService.listUsers();

        List<UserData> expectedUsers = new ArrayList<>();
        expectedUsers.add(user1);
        expectedUsers.add(user3);

        assert (expectedUsers.equals(users));
    }

    @Test
    void logoutPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = UserService.register(user1);
            UserService.logout(register1.authToken());

            assert (MemoryDataAccess.getAuth(register1.authToken()) == null);
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException e) {}


    }

    @Test
    void logoutNegativeTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        try {
            UserService.register(user1);
            assertThrows(UnauthorizedException.class, () -> UserService.logout("not an auth token :PPPPPP"));

        } catch (AlreadyTakenException | BadPasswordException e) {}


    }
    @Test
    void loginPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        try {
            RegisterResult register1 = UserService.register(user1);
            UserService.logout(register1.authToken());
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException e) {}

        try {
            LoginResult loginfo = UserService.login(new LoginRequest(user1.username(), user1.password()));
            assert (MemoryDataAccess.getAuth(loginfo.authToken()) != null);
        } catch (BadPasswordException | MissingUsernameException | BadRequestException e) {}
    }

    @Test
    void loginNegativeTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        try {
            RegisterResult register1 = UserService.register(user1);
            UserService.logout(register1.authToken());
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException e) {}


        assertThrows(MissingUsernameException.class, () -> UserService.login(new LoginRequest("incorrect username", user1.password())));
    }

    @Test
    void createGamePositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = UserService.register(user1);
            CreateResult result1 = UserService.createGame("gamename1", register1.authToken());
            CreateResult result2 = UserService.createGame("gamename2", register1.authToken());

            assert(result1.gameID() == 1001 && result2.gameID() == 1002);
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException | BadRequestException e) {}
    }

    @Test
    void createGameNegativeTest() throws DataAccessException, AlreadyTakenException{
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = UserService.register(user1);
            UserService.createGame("gamename4", register1.authToken());
            assertThrows(AlreadyTakenException.class, () -> UserService.createGame("gamename4", register1.authToken()));
        } catch (UnauthorizedException | BadPasswordException | BadRequestException e) {}
    }

    @Test
    void listGamesPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = UserService.register(user1);
            UserService.createGame("gamename1", register1.authToken());
            UserService.createGame("gamename2", register1.authToken());

            assert(UserService.listGames(register1.authToken()).size() == 2);
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException | BadRequestException e) {}
    }

    @Test
    void listGamesNegativeTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = UserService.register(user1);
            UserService.createGame("gamename1", register1.authToken());
            UserService.createGame("gamename2", register1.authToken());
            assertThrows(UnauthorizedException.class, () -> UserService.listGames("bad token"));
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException | BadRequestException e) {}
    }

    @Test
    void clearDataPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = UserService.register(user1);
            UserService.createGame("gamename1", register1.authToken());
            UserService.createGame("gamename2", register1.authToken());

            UserService.clearAllData();

            var user2 = new UserData("username1", "email1", "password1");
            RegisterResult register2 = UserService.register(user2);
            assert(UserService.listGames(register2.authToken()).isEmpty());
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException | BadRequestException e) {}
    }

    @Test
    void addUserWhitePositiveTest() {

        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = UserService.register(user1);
            UserService.createGame("gamename1", register1.authToken());
            UserService.joinGame(1001, "WHITE", register1.authToken());

            var user2 = new UserData("username1", "email1", "password1");
            UserService.register(user2);

            //get games (first game) should have username1 on white team
            assert(MemoryDataAccess.getGamesList().getFirst().whiteUsername().equals("username1"));
        } catch (AlreadyTakenException | UnauthorizedException | DataAccessException | BadPasswordException | BadRequestException e) {}
    }
    @Test
    void addUserWhiteNegativeTest() {

        //another test
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = UserService.register(user1);
            UserService.createGame("gamename1", register1.authToken());
            UserService.joinGame(1001, "WHITE", register1.authToken());

            var user2 = new UserData("username1", "email1", "password1");
            RegisterResult register2 = UserService.register(user2);

            assertThrows(AlreadyTakenException.class, () -> UserService.joinGame(1001, "WHITE", register2.authToken()));

        } catch (AlreadyTakenException | UnauthorizedException | DataAccessException | BadPasswordException | BadRequestException e) {}
    }
}
