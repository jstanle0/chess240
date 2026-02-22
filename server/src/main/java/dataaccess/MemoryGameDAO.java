package dataaccess;

import chess.ChessGame;
import models.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private int nextId = 0;
    private final HashMap<Integer, GameData> gameTable = new HashMap<>();


    @Override
    public Collection<GameData> getGames(){
        return gameTable.values();
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
    public void updateGame(GameData game) throws DataAccessException {
        gameTable.put(game.gameId(), game);
    }

    @Override
    public void clearTable() {
        gameTable.clear();
    }
}
