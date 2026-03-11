package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import models.GameData;
import models.GamesListResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameDAOTest {
    private static final GameDAO GAME_DAO = DAOs.getGameDAO();

    @BeforeEach
    @AfterEach
    void clear() {
        GAME_DAO.clearTable();
    }

    @Test
    void testClearTable() {
        var data = initializeDAO();
        Assertions.assertDoesNotThrow(GAME_DAO::clearTable);
        Assertions.assertThrows(DataAccessException.class, () -> GAME_DAO.getGame(data.gameID()));
    }

    @Test
    void testCreateGame() {
        Assertions.assertDoesNotThrow(() -> GAME_DAO.createGame("test"));
    }

    @Test
    void testGetGamesEmpty() {
        GamesListResponse res = Assertions.assertDoesNotThrow(GAME_DAO::getGames);
        Assertions.assertTrue(res.games().isEmpty());
    }

    @Test
    void testGetGames() {
        initializeDAO();
        var data = initializeDAO();
        GamesListResponse res = Assertions.assertDoesNotThrow(GAME_DAO::getGames);
        Assertions.assertEquals(2, res.games().size());
        Assertions.assertTrue(res.games().contains(data));
    }

    @Test
    void testGetGame() {
        var data = initializeDAO();
        var game = Assertions.assertDoesNotThrow(() -> GAME_DAO.getGame(data.gameID()));
        Assertions.assertEquals(data, game);
    }

    @Test
    void testFailedGetGame() {
        Assertions.assertThrows(DataAccessException.class, () -> GAME_DAO.getGame(999));
    }

    @Test
    void testUpdateGame() {
        var firstData = initializeDAO();
        var secondData = initializeDAO();
        var newData = new GameData(firstData.gameID(), null, "b", firstData.gameName());
        Assertions.assertDoesNotThrow(() -> GAME_DAO.updateGame(newData));
        var dataFromDb = Assertions.assertDoesNotThrow( () -> GAME_DAO.getGame(newData.gameID()));
        var secondDataFromDb = Assertions.assertDoesNotThrow( () -> GAME_DAO.getGame(secondData.gameID()));
        Assertions.assertEquals(newData, dataFromDb);
        Assertions.assertNotEquals(secondDataFromDb.blackUsername(), dataFromDb.blackUsername());
    }

    @Test
    void testGetGameObject() {
        var data = initializeDAO();
        var game = Assertions.assertDoesNotThrow(() -> GAME_DAO.getGameObject(data.gameID()));
        Assertions.assertEquals(new ChessGame(), game);
    }

    @Test
    void testFailedGetGameObject() {
        Assertions.assertThrows(DataAccessException.class, () -> GAME_DAO.getGameObject(999));
    }

    @Test
    void testUpdateGameObject() {
        var data = initializeDAO();
        var game = Assertions.assertDoesNotThrow( () -> GAME_DAO.getGameObject(data.gameID()));
        Assertions.assertDoesNotThrow(
                () -> game.makeMove(new ChessMove(new ChessPosition(2,1), new ChessPosition(3, 1), null))
        );
        Assertions.assertDoesNotThrow( () -> GAME_DAO.updateGameObject(data.gameID(), game));
        var gameFromDb = Assertions.assertDoesNotThrow( () -> GAME_DAO.getGameObject(data.gameID()));
        Assertions.assertEquals(game, gameFromDb);
    }

    private GameData initializeDAO() {
        return GAME_DAO.createGame("a");
    }
}
