package service;

import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.MemoryDataAccess;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class Phase3Tests {

    static final MySqlDataAccess MEMORY_DATA_ACCESS = new MySqlDataAccess();
    static final UserService USER_SERVICE = new UserService(MEMORY_DATA_ACCESS);


    @BeforeEach
    void clearAll() throws DataAccessException {
        USER_SERVICE.clearAllData();
    }

    @Test
    void registerNewUserPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        var user2 = new UserData("username2", "email2", "password2");
        var user3 = new UserData("username3", "email3", "password3");
        try {
            USER_SERVICE.register(user1);
        } catch (AlreadyTakenException | BadPasswordException e) {}
        try {
            USER_SERVICE.register(user2);
        } catch (AlreadyTakenException e) {} catch (BadPasswordException e) {
            throw new RuntimeException(e);
        }
        try {
            USER_SERVICE.register(user3);
        } catch (AlreadyTakenException | BadPasswordException e) {}
        try {
            List<UserData> users = USER_SERVICE.listUsers();
        List<UserData> expectedUsers = new ArrayList<>();
        expectedUsers.add(user1);
        expectedUsers.add(user2);
        expectedUsers.add(user3);

        assert (expectedUsers.equals(users));

        } catch (SQLException | DataAccessException e) {}

    }

    @Test
    void registerNewUserNegativeTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        var user3 = new UserData("username3", "email3", "password3");
        try {
            USER_SERVICE.register(user1);
        } catch (AlreadyTakenException | BadPasswordException e) {}
        try {
            USER_SERVICE.register(user1);
        } catch (AlreadyTakenException | BadPasswordException e) {}
        try {
            USER_SERVICE.register(user3);
        } catch (AlreadyTakenException | BadPasswordException e) {}

        try {
            List<UserData> users = USER_SERVICE.listUsers();
        List<UserData> expectedUsers = new ArrayList<>();
        expectedUsers.add(user1);
        expectedUsers.add(user3);

        assert (expectedUsers.equals(users));

        } catch (SQLException | DataAccessException e) {}
    }

    @Test
    void logoutPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = USER_SERVICE.register(user1);
            USER_SERVICE.logout(register1.authToken());

            assert (MEMORY_DATA_ACCESS.getAuth(register1.authToken()) == null);
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException e) {}


    }

    @Test
    void logoutNegativeTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        try {
            USER_SERVICE.register(user1);
            assertThrows(UnauthorizedException.class, () -> USER_SERVICE.logout("not an auth token :PPPPPP"));

        } catch (AlreadyTakenException | BadPasswordException e) {}


    }
    @Test
    void loginPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        try {
            RegisterResult register1 = USER_SERVICE.register(user1);
            USER_SERVICE.logout(register1.authToken());
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException e) {}

        try {
            LoginResult loginfo = USER_SERVICE.login(new LoginRequest(user1.username(), user1.password()));
            assert (MEMORY_DATA_ACCESS.getAuth(loginfo.authToken()) != null);
        } catch (BadPasswordException | MissingUsernameException | BadRequestException e) {}
    }

    @Test
    void loginNegativeTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        try {
            RegisterResult register1 = USER_SERVICE.register(user1);
            USER_SERVICE.logout(register1.authToken());
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException e) {}


        assertThrows(MissingUsernameException.class, () -> USER_SERVICE.login(new LoginRequest("incorrect username", user1.password())));
    }

    @Test
    void createGamePositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = USER_SERVICE.register(user1);
            CreateResult result1 = USER_SERVICE.createGame("gamename1", register1.authToken());
            CreateResult result2 = USER_SERVICE.createGame("gamename2", register1.authToken());

            assert(result1.gameID() == 1001 && result2.gameID() == 1002);
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException | BadRequestException e) {}
    }

    @Test
    void createGameNegativeTest() throws DataAccessException, AlreadyTakenException{
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = USER_SERVICE.register(user1);
            USER_SERVICE.createGame("gamename4", register1.authToken());
            assertThrows(AlreadyTakenException.class, () -> USER_SERVICE.createGame("gamename4", register1.authToken()));
        } catch (UnauthorizedException | BadPasswordException | BadRequestException e) {}
    }

    @Test
    void listGamesPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = USER_SERVICE.register(user1);
            USER_SERVICE.createGame("gamename1", register1.authToken());
            USER_SERVICE.createGame("gamename2", register1.authToken());

            assert(USER_SERVICE.listGames(register1.authToken()).size() == 2);
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException | BadRequestException |
                 SQLException e) {}
    }

    @Test
    void listGamesNegativeTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = USER_SERVICE.register(user1);
            USER_SERVICE.createGame("gamename1", register1.authToken());
            USER_SERVICE.createGame("gamename2", register1.authToken());
            assertThrows(UnauthorizedException.class, () -> USER_SERVICE.listGames("bad token"));
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException | BadRequestException e) {}
    }

    @Test
    void clearDataPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = USER_SERVICE.register(user1);
            USER_SERVICE.createGame("gamename1", register1.authToken());
            USER_SERVICE.createGame("gamename2", register1.authToken());

            USER_SERVICE.clearAllData();

            var user2 = new UserData("username1", "email1", "password1");
            RegisterResult register2 = USER_SERVICE.register(user2);
            assert(USER_SERVICE.listGames(register2.authToken()).isEmpty());
        } catch (AlreadyTakenException | UnauthorizedException | BadPasswordException | BadRequestException |
                 SQLException e) {}
    }

    @Test
    void addUserWhitePositiveTest() {

        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = USER_SERVICE.register(user1);
            USER_SERVICE.createGame("gamename1", register1.authToken());
            USER_SERVICE.joinGame(1001, "WHITE", register1.authToken());

            var user2 = new UserData("username1", "email1", "password1");
            USER_SERVICE.register(user2);

            //get games (first game) should have username1 on white team
            assert(MEMORY_DATA_ACCESS.getGamesList().getFirst().whiteUsername().equals("username1"));
        } catch (AlreadyTakenException | UnauthorizedException | DataAccessException | BadPasswordException | BadRequestException | SQLException e) {}
    }
    @Test
    void addUserWhiteNegativeTest() {

        //another test
        var user1 = new UserData("username1", "email1", "password1");

        try {
            RegisterResult register1 = USER_SERVICE.register(user1);
            USER_SERVICE.createGame("gamename1", register1.authToken());
            USER_SERVICE.joinGame(1001, "WHITE", register1.authToken());

            var user2 = new UserData("username1", "email1", "password1");
            RegisterResult register2 = USER_SERVICE.register(user2);

            assertThrows(AlreadyTakenException.class, () -> USER_SERVICE.joinGame(1001, "WHITE", register2.authToken()));

        } catch (AlreadyTakenException | UnauthorizedException | DataAccessException | BadPasswordException | BadRequestException e) {}
    }
}
