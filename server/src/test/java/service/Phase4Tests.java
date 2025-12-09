package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MySqlDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.MemoryDataAccess;

import javax.xml.crypto.Data;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class Phase4Tests {

    static final MySqlDataAccess MEMORY_DATA_ACCESS;

    static {
        MEMORY_DATA_ACCESS = new MySqlDataAccess();
    }

    //don't user USER_SERVICE in these tests blehhh

    @BeforeEach
    void clearAll() throws DataAccessException {
        DatabaseManager.createDatabase();
        MEMORY_DATA_ACCESS.clearAuthData();
        MEMORY_DATA_ACCESS.clearGameData();
        MEMORY_DATA_ACCESS.clearUserData();
    }

    //ALL THE THINGS I NEED TO TEST
    //public UserData addUser(UserData)
    @Test
    void addUserPositiveTest() throws DataAccessException, SQLException {
        var user1 = new UserData("username1", "email1", "password1");
        var user2 = new UserData("username2", "email2", "password2");
        var user3 = new UserData("username3", "email3", "password3");
        MEMORY_DATA_ACCESS.addUser(user1);
        MEMORY_DATA_ACCESS.addUser(user2);
        MEMORY_DATA_ACCESS.addUser(user3);
        List<UserData> expectedUsers = new ArrayList<>();
        expectedUsers.add(user1);
        expectedUsers.add(user2);
        expectedUsers.add(user3);

        List<UserData> returnedUsers = MEMORY_DATA_ACCESS.listUsers();
        boolean areSame = true;
        int numOfMatches = 0;
        for (UserData user11 : expectedUsers) {
            for (UserData user22 : returnedUsers) {
                if (user11.username().equals(user22.username())) {
                    numOfMatches++;
                }
            }
        }
        assert(numOfMatches == 3);
    }

    @Test
    void addUserNegativeTest() throws SQLException {
        var user1 = new UserData("username1", "email1", "password1");
        try {
            MEMORY_DATA_ACCESS.addUser(user1);
        } catch (DataAccessException e) {
            System.out.println("oosies!");
        }
        assertThrows(DataAccessException.class, () ->
                MEMORY_DATA_ACCESS.addUser(user1));

    }

    //public String addAuth(String username)
    @Test
    void addAuthPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        MEMORY_DATA_ACCESS.addUser(user1);
        String auth1 = MEMORY_DATA_ACCESS.addAuth(user1.username());
        assert(MEMORY_DATA_ACCESS.getAuth(auth1).username().equals(user1.username()));
    }

    @Test
    void addAuthNegativeTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        MEMORY_DATA_ACCESS.addUser(user1);
        String auth1 = MEMORY_DATA_ACCESS.addAuth(user1.username());
        assert(MEMORY_DATA_ACCESS.getAuth("blwabaoienpoiaEVniowp") == null);
    }

    //public CreateResult createNewGame(String gamename)
    @Test
    void createGamePositiveTest() throws DataAccessException, AlreadyTakenException, SQLException {
        MEMORY_DATA_ACCESS.clearGameData();
        CreateResult result1 = MEMORY_DATA_ACCESS.createNewGame("game1");
        CreateResult result2 = MEMORY_DATA_ACCESS.createNewGame("game2");

        System.out.println(MEMORY_DATA_ACCESS.getGamesList());

        assert(result1.gameID() + 1 == result2.gameID());

    }

    @Test
    void createGameNegativeTest() throws DataAccessException, AlreadyTakenException {
        CreateResult result1 = MEMORY_DATA_ACCESS.createNewGame("game1");


        assertThrows(AlreadyTakenException.class, () -> MEMORY_DATA_ACCESS.createNewGame("game1"));
    }

    //public void removeAuth(Authdata auth)
    @Test
    void removeAuthPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        String authToken = MEMORY_DATA_ACCESS.addAuth(user1.username());
        AuthData realAuth = new AuthData(authToken, user1.username());
        MEMORY_DATA_ACCESS.removeAuth(realAuth);

        assert (MEMORY_DATA_ACCESS.getAuth(authToken) == null);
    }

    @Test
    void removeAuthNegativeTest() throws DataAccessException {
        AuthData fakeAuth = new AuthData("blahblahblah", "Kind_username");
        MEMORY_DATA_ACCESS.removeAuth(fakeAuth);

        assert (MEMORY_DATA_ACCESS.getAuth("blahblahblah") == null);
    }

    //public UserData getUser(String username)
    @Test
    void getUserPositiveTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        MEMORY_DATA_ACCESS.addUser(user1);
        UserData returnedUser = MEMORY_DATA_ACCESS.getUser("username1");

        assert(user1.email().equals(returnedUser.email()));
    }

    @Test
    void getUserNegativeTest() throws DataAccessException {
        var user1 = new UserData("username1", "email1", "password1");
        MEMORY_DATA_ACCESS.addUser(user1);

        assert(MEMORY_DATA_ACCESS.getUser("fakeUsername") == null);
    }

    //public JoinResult joinExistingGame(int GameID, String color, String username)
    @Test
    void joinExistingGamePositiveTest() throws DataAccessException, AlreadyTakenException, BadRequestException {
        var user1 = new UserData("username1", "email1", "password1");
        CreateResult result1 = MEMORY_DATA_ACCESS.createNewGame("game1");

        JoinResult swag = MEMORY_DATA_ACCESS.joinExistingGame(result1.gameID(), "WHITE", user1.username());
        assert(swag.equals(new JoinResult()));
    }

    @Test
    void joinExistingGameNegativeTest() throws DataAccessException, AlreadyTakenException, BadRequestException {
        var user1 = new UserData("username1", "email1", "password1");
        CreateResult result1 = MEMORY_DATA_ACCESS.createNewGame("game1");
        MEMORY_DATA_ACCESS.joinExistingGame(result1.gameID(), "WHITE", user1.username());

        assertThrows(AlreadyTakenException.class, () -> MEMORY_DATA_ACCESS.joinExistingGame(result1.gameID(), "WHITE", user1.username()));
    }

    //public List<GameData> getGamesList()
    @Test
    void getGamesListPositiveTest() throws DataAccessException, AlreadyTakenException, SQLException {
        MEMORY_DATA_ACCESS.createNewGame("game1");
        List<GameData> returnedGameList = MEMORY_DATA_ACCESS.getGamesList();


        assert(returnedGameList.getLast().gameID() == 1001);
    }

    @Test
    void getGamesListNegativeTest() throws DataAccessException, SQLException {
        List<GameData> returnedGameList = MEMORY_DATA_ACCESS.getGamesList();
        List<GameData> expected = new ArrayList<>();
        assert(returnedGameList.equals(expected));
    }

    //public AuthData getAuth(String token)
    @Test
    void getAuthPositiveTest () throws DataAccessException {
        String auth1 = MEMORY_DATA_ACCESS.addAuth("username");
        AuthData expectedData = new AuthData(auth1, "username");

        assert(MEMORY_DATA_ACCESS.getAuth(auth1).equals(expectedData));
    }

    @Test
    void getAuthNegativeTest () throws DataAccessException {
        assert(MEMORY_DATA_ACCESS.getAuth("fake auth") == null);
    }

    //public List<UserData> listUsers()
    @Test
    void listUsersPositiveTest () throws DataAccessException, SQLException {
        var user1 = new UserData("username1", "email1", "password1");
        var user2 = new UserData("username1", "email1", "password1");
        var user3 = new UserData("username1", "email1", "password1");
        MEMORY_DATA_ACCESS.addUser(user1);
        MEMORY_DATA_ACCESS.addUser(user2);
        MEMORY_DATA_ACCESS.addUser(user3);

        assert (MEMORY_DATA_ACCESS.listUsers().getFirst() == user1);
    }

    @Test
    void listUsersNegativeTest () throws DataAccessException, SQLException {
        assert (MEMORY_DATA_ACCESS.listUsers()== null);
    }

    //public void clearUser/Auth/GameData
    @Test
    void clearAllPositiveTest () throws DataAccessException, SQLException {
        var user1 = new UserData("username1", "email1", "password1");
        MEMORY_DATA_ACCESS.addUser(user1);
        MEMORY_DATA_ACCESS.addAuth(user1.username());

        MEMORY_DATA_ACCESS.clearGameData();
        MEMORY_DATA_ACCESS.clearAuthData();
        MEMORY_DATA_ACCESS.clearUserData();

        assert (MEMORY_DATA_ACCESS.listUsers() == null && MEMORY_DATA_ACCESS.getGamesList() == null);
    }


}
