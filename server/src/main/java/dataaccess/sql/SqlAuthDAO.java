package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import models.AuthData;

import java.util.UUID;

public class SqlAuthDAO extends SqlHelpers implements AuthDAO {
    @Override
    public String getUsernameFromToken(UUID token) throws DataAccessException {
        return "";
    }

    @Override
    public void createAuth(AuthData data) {

    }

    @Override
    public void deleteAuth(UUID token) throws DataAccessException {

    }

    @Override
    public void clearTable() {
        String statement = "TRUNCATE auth";
        try { executeUpdate(statement); }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
