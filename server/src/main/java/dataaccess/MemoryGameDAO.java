package dataaccess;

import models.GameData;
import models.GamesListResponse;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private int nextId = 0;
    private final HashMap<Integer, GameData> gameTable = new HashMap<>();


    @Override
    public GamesListResponse getGames(){
        return new GamesListResponse(gameTable.values());
    }

    @Override
    public GameData getGame(Integer gameId) throws DataAccessException {
        GameData game = gameTable.get(gameId);
        if (game == null) {
            throw new DataAccessException("game not found");
        }
        return game;
    }

    @Override
    public GameData createGame(String gameName) {
        GameData data = new GameData(nextId, "", "", gameName);
        gameTable.put(nextId, data);
        nextId++;
        return data;
    }

    @Override
    public void updateGame(GameData game) {
        gameTable.put(game.gameID(), game);
    }

    @Override
    public void clearTable() {
        gameTable.clear();
    }
}
