package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import models.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SqlAuthDAO extends SqlHelpers implements AuthDAO {
    private String rsToString(ResultSet rs) {
        try {
            return rs.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUsernameFromToken(UUID token) throws DataAccessException {
        String statement = "SELECT username FROM auth WHERE token = ?";
        try {
            var result = executeQuery(statement, this::rsToString, token.toString());
            for (var s : result) {
                return s;
            }
            throw new DataAccessException("no active session found");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createAuth(AuthData data) {
        String statement = "INSERT INTO auth (token, username) VALUES (?, ?)";
        try {
            executeUpdate(statement, data.authToken().toString(), data.username());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuth(UUID token) throws DataAccessException {
        String statement = "DELETE FROM auth WHERE token = ?";
        try {
            int result = executeUpdate(statement, token.toString());
            if (result == 0) {
                throw new DataAccessException("no active session");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
