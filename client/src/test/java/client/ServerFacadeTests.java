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
        String result = client.register(params);

        assertEquals("Successfully logged out!", result);
    }

    @Test
    void logoutNegativeTest() throws Exception {
        String result = client.logout();
        assertEquals("""
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - to exit the program
                    help - displays possible commands
                    """, result);
    }

}
