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

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
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
        facade = new ServerFacade(url);
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
        UserData user1 = new UserData("username13", "password1", "email1");
        RegisterResult result = facade.addUser(user1);

        System.out.println( "AAAAAAAH" + result);
        Assertions.assertEquals("username13", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    void registerUserNegativeTest() throws Exception {
        UserData user1 = new UserData("username14", "password1", "email1");
        facade.addUser(user1);
        UserData user2 = new UserData("username12", "password", "email1");
        facade.addUser(user2);
        Assertions.assertThrows(NullPointerException.class, () -> facade.addUser(user1));
    }

}
