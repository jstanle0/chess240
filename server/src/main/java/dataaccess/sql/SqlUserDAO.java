package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import models.UserData;

public class SqlUserDAO extends SqlHelpers implements UserDAO {

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createUser(UserData data) {
        String statement = """
                INSERT INTO user (username, password, email) VALUES (?, ?, ?);
                """;
        try { executeUpdate(statement, data.username(), data.password(), data.email()); }
        catch (DataAccessException e) {
            throw new RuntimeException(e); // Throws 500
        }
    }

    @Override
    public void clearTable() {
        String statement = "TRUNCATE user";
        try { executeUpdate(statement); }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
