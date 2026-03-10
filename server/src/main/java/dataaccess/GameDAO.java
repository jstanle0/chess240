package dataaccess;

import chess.ChessGame;
import models.GameData;
import models.GamesListResponse;

public interface GameDAO {
    GamesListResponse getGames();
    GameData getGame(Integer gameId) throws DataAccessException;
    GameData createGame(String gameName);
    void updateGame(GameData game);
    void clearTable();
    ChessGame getGameObject(Integer gameId) throws DataAccessException;
    void updateGameObject(Integer gameId, ChessGame newGame);
}
