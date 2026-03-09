package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import models.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlUserDAO extends SqlHelpers implements UserDAO {

    private UserData rsToUserData(ResultSet rs) {
        try {
            return new UserData(
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String query = "SELECT username, password, email FROM user WHERE username = ?";
        ResultSet rs;
        try {
            var result = executeQuery(query, this::rsToUserData ,username);
            for (var user : result) {
                return user;
            }
            throw new DataAccessException("no user found");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user: " + e);
        }
    }

    @Override
    public void createUser(UserData data) {
        String statement = """
                INSERT INTO user (username, password, email) VALUES (?, ?, ?);
                """;
        try { executeUpdate(statement, data.username(), data.password(), data.email()); }
        catch (SQLException e) {
            throw new RuntimeException("Failed to create user: " + e); // Throws 500
        }
    }

    @Override
    public void clearTable() {
        String statement = "TRUNCATE user";
        try { executeUpdate(statement); }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
