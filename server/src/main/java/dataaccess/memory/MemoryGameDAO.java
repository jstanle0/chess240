package dataaccess.memory;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import models.GameData;
import models.GamesListResponse;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private int nextId = 1;
    private final HashMap<Integer, GameData> gameTable = new HashMap<>();
    private final HashMap<Integer, ChessGame> gameObjectTable = new HashMap<>();


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
        GameData data = new GameData(nextId, null, null, gameName);
        gameTable.put(nextId, data);
        gameObjectTable.put(nextId, new ChessGame());
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
        gameObjectTable.clear();
    }

    @Override
    public ChessGame getGameObject(Integer gameId) throws DataAccessException {
        ChessGame game = gameObjectTable.get(gameId);
        if (game == null) {
            throw new DataAccessException("no game object found");
        }
        return game;
    }

    @Override
    public void updateGameObject(Integer gameId, ChessGame newGame) {
        gameObjectTable.put(gameId, newGame);
    }
}
