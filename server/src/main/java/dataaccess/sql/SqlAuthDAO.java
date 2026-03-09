package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import models.AuthData;

import java.sql.SQLException;
import java.util.UUID;

public class SqlAuthDAO extends SqlHelpers implements AuthDAO {
    @Override
    public String getUsernameFromToken(UUID token) throws DataAccessException {
        return "";
    }

    @Override
    public void createAuth(AuthData data) {
        String statement = "INSERT INTO auth (token, username) VALUES (?, ?)";
        try {
            executeUpdate(statement, data.authToken(), data.username());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuth(UUID token) throws DataAccessException {

    }

    @Override
    public void clearTable() {
        String statement = "TRUNCATE auth";
        try { executeUpdate(statement); }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
