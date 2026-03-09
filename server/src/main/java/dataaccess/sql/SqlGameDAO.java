package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import models.GameData;
import models.GamesListResponse;

import java.sql.SQLException;

public class SqlGameDAO extends SqlHelpers implements GameDAO {
    @Override
    public GamesListResponse getGames() {
        return null;
    }

    @Override
    public GameData getGame(Integer gameId) throws DataAccessException {
        return null;
    }

    @Override
    public GameData createGame(String gameName) {
        return null;
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public void clearTable() {
        String statement = "TRUNCATE game";
        try { executeUpdate(statement); }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
