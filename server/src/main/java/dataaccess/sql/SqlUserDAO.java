package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import models.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlUserDAO implements UserDAO {
    private static final Logger log = LoggerFactory.getLogger(SqlUserDAO.class);

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createUser(UserData data) {

    }

    @Override
    public void clearTable() {

    }
}
