package dataaccess;

import models.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GameData> getGames();
    GameData getGame(Integer gameId) throws DataAccessException;
    GameData createGame(String gameName);
    void updateGame(GameData game);
    void clearTable();
}
