package client;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.UnauthorizedException;
import service.UserService;
import ui.ChessClient;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ChessClient client;
    static final MySqlDataAccess MEMORY_DATA_ACCESS;

    static {
        MEMORY_DATA_ACCESS = new MySqlDataAccess();
    }


    @BeforeAll
    public static void init() {
        var service = new UserService(new MySqlDataAccess());
        server = new Server(service);
        var port = server.run(0);
        var url = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
        client = new ChessClient(url);
    }

    @BeforeEach
    void clearAll() throws DataAccessException {
        DatabaseManager.createDatabase();
        MEMORY_DATA_ACCESS.clearUserData();
        MEMORY_DATA_ACCESS.clearAuthData();
        MEMORY_DATA_ACCESS.clearGameData();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }


    @Test
    void registerUserPositiveTest() throws Exception {
        String[] params1 = new String[]{"username13", "password1", "email1"};
        String result = client.register(params1);

        assertEquals("Successfully registered and logged in as username13", result);
    }

    @Test
    void registerUserNegativeTest() throws Exception {
        String[] params1 = new String[]{"username14", "password1", "email1"};
        String[] params2 = new String[]{"username14", "password1", "email1"};
        client.register(params1);
        assertEquals("Username already taken", client.register(params2));
    }

    @Test
    void logoutPositiveTest() throws Exception {
        String[] params = new String[]{"username14", "password1", "email1"};
        client.register(params);
        String result = client.logout();

        assertEquals("Successfully logged you out", result);
    }

    @Test
    void logoutNegativeTest() throws Exception {
        String result = client.logout();
        assertEquals("Successfully logged you out", result);
        //this one is the same as ServerFacadeTests bypass the State thingy
    }

    @Test
    void loginPositiveTest() throws Exception {
        String[] params = new String[]{"username14", "password1", "email1"};
        client.register(params);
        client.logout();
        String result = client.login("username14", "password1");
        assertEquals("Successfully logged in as username14", result);

    }

    @Test
    void loginNegativeTest1() throws Exception {
        String result = client.login("username14", "password1");
        assertEquals("Invalid username/password", result);

    }

    @Test
    void loginNegativeTest2() throws Exception {
        Assertions.assertThrows(ResponseException.class, () -> client.login("onlyUsername"));
    }

    @Test
    void createGamePositiveTest() throws ResponseException {
        String[] params1 = new String[]{"username13", "password1", "email1"};
        client.register(params1);
        String result = client.createGame("swag");
        assertEquals("Game swag successfully created", result);
    }

    @Test
    void createGameNegativeTest() throws ResponseException {
        String[] params1 = new String[]{"username13", "password1", "email1"};
        client.register(params1);
        client.createGame("swag");
        String result = client.createGame("swag");
        assertEquals("That name is already taken, please choose a new one", result);
    }

    @Test
    void listGamesPositiveTest() throws ResponseException {
        String[] params1 = new String[]{"username13", "password1", "email1"};
        client.register(params1);
        client.createGame("swag1");
        client.createGame("swag2");
        client.createGame("swag3");
        String result = client.listGames();
        assertEquals(
                """
                
                ---------------------------------------------------------
                | Game ID  | Game Name  | White Player  | Black Player  |
                | 1        | swag1      | AVAILABLE     | AVAILABLE     |
                | 2        | swag2      | AVAILABLE     | AVAILABLE     |
                | 3        | swag3      | AVAILABLE     | AVAILABLE     |
                ---------------------------------------------------------
                """, result);
    }

    @Test
    void listGamesNegativeTest() throws ResponseException {
        String result = client.listGames();
        assertEquals("No games in database, create a new one to begin", result);
    }

    @Test
    void joinGamePositiveTest() throws ResponseException {
        String[] params1 = new String[]{"username13", "password1", "email1"};
        String[] params2 = new String[]{"1", "WHITE"};
        client.register(params1);
        client.createGame("swag1");
        client.createGame("swag2");
        client.createGame("swag3");
        client.listGames();
        String result = client.joinGame(params2);
        assertEquals("""
Game 1 successfully joined
[48;5;242m   [49m[48;5;242mA [49m[48;5;242m  B [49m[48;5;242m  C [49m[48;5;242m  D [49m[48;5;242m E [49m[48;5;242m  F [49m[48;5;242m  G [49m[48;5;242m  H[49m[48;5;242m   [49m
[48;5;242m 8 [49m[48;5;235m ♖ [49m[48;5;0m ♘ [49m[48;5;235m ♗ [49m[48;5;0m ♕ [49m[48;5;235m ♔ [49m[48;5;0m ♗ [49m[48;5;235m ♘ [49m[48;5;0m ♖ [49m[48;5;242m 8 [49m
[48;5;242m 7 [49m[48;5;0m ♙ [49m[48;5;235m ♙ [49m[48;5;0m ♙ [49m[48;5;235m ♙ [49m[48;5;0m ♙ [49m[48;5;235m ♙ [49m[48;5;0m ♙ [49m[48;5;235m ♙ [49m[48;5;242m 7 [49m
[48;5;242m 6 [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;242m 6 [49m
[48;5;242m 5 [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;242m 5 [49m
[48;5;242m 4 [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;242m 4 [49m
[48;5;242m 3 [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;0m   [49m[48;5;235m   [49m[48;5;242m 3 [49m
[48;5;242m 2 [49m[48;5;235m ♟ [49m[48;5;0m ♟ [49m[48;5;235m ♟ [49m[48;5;0m ♟ [49m[48;5;235m ♟ [49m[48;5;0m ♟ [49m[48;5;235m ♟ [49m[48;5;0m ♟ [49m[48;5;242m 2 [49m
[48;5;242m 1 [49m[48;5;0m ♜ [49m[48;5;235m ♞ [49m[48;5;0m ♝ [49m[48;5;235m ♛ [49m[48;5;0m ♚ [49m[48;5;235m ♝ [49m[48;5;0m ♞ [49m[48;5;235m ♜ [49m[48;5;242m 1 [49m
[48;5;242m   [49m[48;5;242mA [49m[48;5;242m  B [49m[48;5;242m  C [49m[48;5;242m  D [49m[48;5;242m E [49m[48;5;242m  F [49m[48;5;242m  G [49m[48;5;242m  H[49m[48;5;242m   [49m\n""", result);
    }

    @Test
    void joinGameNegativeTest() throws ResponseException {
        String[] params1 = new String[]{"username13", "password1", "email1"};
        String[] params2 = new String[]{"1", "WHITE"};
        client.register(params1);
        client.createGame("swag1");
        client.createGame("swag2");
        client.createGame("swag3");
        client.listGames();
        client.joinGame(params2);
        String result = client.joinGame(params2);
        assertEquals("Unable to join that game/color, please try a different one", result);
    }

}
