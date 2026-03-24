package client;

import http.ServerFacade;
import models.LoginUserData;
import models.ResponseException;
import models.UserData;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:8080");
        try {
            serverFacade.clearDB();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        try {
            serverFacade.clearDB();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        server.stop();
    }

    @Test
    public void testClearDB() {
        try {
            serverFacade.createAccount(new UserData("test", "test", "test"));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertDoesNotThrow(() -> serverFacade.clearDB());
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(new LoginUserData("test", "test")));
    }

}
