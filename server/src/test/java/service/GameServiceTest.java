package service;

import chess.ChessGame;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import models.GameData;
import models.JoinGameBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameServiceTest extends UnitTestUtils {
    @Test
    public void createGameTest() {
        GameData newGame = Assertions.assertDoesNotThrow(() -> GameService.createGame("game 2"));
        Assertions.assertNotEquals(existingGame.gameID(), newGame.gameID());
    }

    @Test
    public void getGameTest() {
        Assertions.assertEquals(1, gameDAO.getGames().games().size());
        GameData newGame = gameDAO.createGame("game 2");
        Assertions.assertEquals(2, gameDAO.getGames().games().size());
        Assertions.assertTrue(gameDAO.getGames().games().contains(newGame));
    }

    @Test
    public void joinGameTest() {
        JoinGameBody body = new JoinGameBody(ChessGame.TeamColor.WHITE, existingGame.gameID());
        Assertions.assertDoesNotThrow(() -> GameService.joinGame(body, existingAuthToken.toString()));
        GameData updatedGame = Assertions.assertDoesNotThrow(() -> gameDAO.getGame(existingGame.gameID()));
        Assertions.assertEquals(existingUser.username(), updatedGame.whiteUsername());
        Assertions.assertNull(updatedGame.blackUsername());
    }

    @Test
    public void alreadyJoinedGameTest() {
        JoinGameBody body = new JoinGameBody(ChessGame.TeamColor.BLACK, existingGame.gameID());
        Assertions.assertDoesNotThrow(() -> GameService.joinGame(body, existingAuthToken.toString()));
        Assertions.assertThrows(ForbiddenResponse.class, () -> GameService.joinGame(body, existingAuthToken.toString()));
    }

    @Test
    public void joinNonexistentGameTest() {
        JoinGameBody body = new JoinGameBody(ChessGame.TeamColor.BLACK, 99999);
        Assertions.assertThrows(BadRequestResponse.class, () -> GameService.joinGame(body, existingAuthToken.toString()));
    }
}
