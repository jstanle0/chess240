package client;

import chess.ChessGame;
import com.mysql.cj.log.Log;
import http.ServerFacade;
import models.*;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.UUID;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:8080");
    }
    @BeforeEach
    public void clear() {
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
        createTestAccount();
        Assertions.assertDoesNotThrow(() -> serverFacade.clearDB());
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(new LoginUserData("test", "test")));
    }

    @Test
    public void testCreateAccount() {
        var authData = Assertions.assertDoesNotThrow(() -> serverFacade.createAccount(new UserData("test", "test", "test")));
        var secondAuthData = Assertions.assertDoesNotThrow(() -> serverFacade.createAccount(new UserData("test2", "test2", "test2")));
        Assertions.assertNotEquals(authData.authToken(), secondAuthData.authToken());
    }

    @Test
    public void testTakenCreateAccount() {
        Assertions.assertDoesNotThrow(() -> serverFacade.createAccount(new UserData("test", "test", "test")));
        var e = Assertions.assertThrows(ResponseException.class, () -> serverFacade.createAccount(new UserData("test", "", "test")));
        Assertions.assertEquals(403, e.getCode());
    }

    @Test
    public void testLogin() {
        var createAuthData = createTestAccount();
        var loginAuthData = Assertions.assertDoesNotThrow(() -> serverFacade.login(new LoginUserData("test", "test")));
        Assertions.assertNotEquals(createAuthData, loginAuthData);
    }

    @Test
    public void testIncorrectLogin() {
        createTestAccount();
        var e = Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(new LoginUserData("test", "dsafdjsalfj")));
        Assertions.assertEquals(401, e.getCode());
    }

    @Test
    public void testNonexistentLogin() {
        createTestAccount();
        var e = Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(new LoginUserData("fakeUser", "test")));
        Assertions.assertEquals(401, e.getCode());
    }

    @Test
    public void testLogout() {
        var authData = createTestAccount();
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(authData.authToken().toString()));
        var e = Assertions.assertThrows(ResponseException.class, () -> serverFacade.listGames(authData.authToken().toString()));
        Assertions.assertEquals(401, e.getCode());
    }

    @Test
    public void testDuplicateLogout() {
        var authData = createTestAccount();
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(authData.authToken().toString()));
        var e = Assertions.assertThrows(ResponseException.class, () -> serverFacade.logout(authData.authToken().toString()));
        Assertions.assertEquals(401, e.getCode());
    }

    @Test
    public void testCreateGame() {
        var authData = createTestAccount();
        var game = Assertions.assertDoesNotThrow(() -> serverFacade.createGame(new CreateGameBody("test"), authData.authToken().toString()));
        Assertions.assertEquals(Integer.class, game.gameID().getClass());
        Assertions.assertEquals("test", game.gameName());
    }

    @Test
    public void testUnauthorizedCreateGame() {
        createTestAccount();
        var e = Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(new CreateGameBody("test"), "asfdsklafjkleajfkl"));
        Assertions.assertEquals(401, e.getCode());
    }

    @Test
    public void testListGames() {
        var authData = createTestAccount();
        var game1 = createTestGame(authData.authToken(), "game1");
        createTestGame(authData.authToken(), "game2");
        var gameList = Assertions.assertDoesNotThrow(() -> serverFacade.listGames(authData.authToken().toString()));
        Assertions.assertEquals(2, gameList.games().size());
        Assertions.assertTrue(gameList.games().contains(game1));
    }

    @Test
    public void testEmptyListGames() {
        var authData = createTestAccount();
        var res = Assertions.assertDoesNotThrow(() -> serverFacade.listGames(authData.authToken().toString()));
        Assertions.assertTrue(res.games().isEmpty());
    }

    @Test
    public void testJoinGame() {
        var authData = createTestAccount();
        var game1 = createTestGame(authData.authToken(), "game1");
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(new JoinGameBody(ChessGame.TeamColor.WHITE, game1.gameID()), authData.authToken().toString()));
        var gameList = Assertions.assertDoesNotThrow(() -> serverFacade.listGames(authData.authToken().toString()));
        Assertions.assertEquals(authData.username(), gameList.games().stream().toList().getFirst().whiteUsername());
    }

    @Test
    public void testDuplicateJoinGame() {
        var authData = createTestAccount();
        var game1 = createTestGame(authData.authToken(), "game1");
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(new JoinGameBody(ChessGame.TeamColor.WHITE, game1.gameID()), authData.authToken().toString()));
        var e = Assertions.assertThrows(ResponseException.class, () -> serverFacade.joinGame(new JoinGameBody(ChessGame.TeamColor.WHITE, game1.gameID()), authData.authToken().toString()));
        Assertions.assertEquals(403, e.getCode());
    }

    private AuthData createTestAccount() {
        try {
            return serverFacade.createAccount(new UserData("test", "test", "test"));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private GameData createTestGame(UUID authToken, String name) {
        try {
            return serverFacade.createGame(new CreateGameBody(name), authToken.toString());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }
}
