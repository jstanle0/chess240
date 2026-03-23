package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import models.GameData;
import models.GamesListResponse;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlGameDAO extends SqlHelpers implements GameDAO {

    private GameData rsToGameData(ResultSet rs) {
        try {
            return new GameData(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GamesListResponse getGames() {
        String query = "SELECT game_id, white_username, black_username, game_name FROM game ORDER BY game_id";
        try {
            return new GamesListResponse(executeQuery(query, this::rsToGameData));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData getGame(Integer gameId) throws DataAccessException {
        String query = "SELECT game_id, white_username, black_username, game_name FROM game WHERE game_id = ?";
        try {
            var response = executeQuery(query, this::rsToGameData, gameId);
            for (var game : response) {
                return game;
            }
            throw new DataAccessException("No game found");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData createGame(String gameName) {
        String statement = "INSERT INTO game (game_name, game) VALUES (?, ?)";
        try {
            var gson = new Gson();
            var game = new ChessGame();
            var id = executeUpdate(statement, gameName, gson.toJson(game));
            return new GameData(id, null, null, gameName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create game: " + e);
        }
    }

    @Override
    public void updateGame(GameData game) {
        String statement = "UPDATE game SET white_username = ?, black_username = ? WHERE game_id = ?";
        try {
            executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameID());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update game: " + e);
        }
    }

    @Override
    public void clearTable() {
        String statement = "TRUNCATE game";
        try { executeUpdate(statement); }
        catch (SQLException e) {
            throw new RuntimeException("Failed to clear table: " + e);
        }
    }

    @Override
    public ChessGame getGameObject(Integer gameId) throws DataAccessException {
        String query = "SELECT game FROM game WHERE game_id = ?";
        try {
            var gson = new Gson();
            var response = executeQuery(query, this::rsToString, gameId);
            for (var r : response) {
                return gson.fromJson(r, ChessGame.class);
            }
            throw new DataAccessException("No game found");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateGameObject(Integer gameId, ChessGame newObject) {
        String statement = "UPDATE game SET game = ? WHERE game_id = ?";
        try {
            var gson = new Gson();
            executeUpdate(statement, gson.toJson(newObject), gameId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update game: " + e);
        }
    }
}
