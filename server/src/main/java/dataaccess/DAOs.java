package dataaccess;

import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.sql.SqlAuthDAO;
import dataaccess.sql.SqlGameDAO;
import dataaccess.sql.SqlUserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that globally instantiates and serves DAOs
 */
public class DAOs {
    private static final Logger LOGGER = LoggerFactory.getLogger(DAOs.class);
    // toggle this boolean to change between memory and SQL DAOs
    private static Boolean useDB = true;
    private static final AuthDAO AUTH_DAO;
    private static final UserDAO USER_DAO;
    private static final GameDAO GAME_DAO;

    static {
        if (useDB) {
            try {
                DatabaseManager.createDatabase();
                DatabaseManager.instantiateTables();
            } catch (DataAccessException e) {
                LOGGER.error("Failed to connect to database. Defaulting to memory.");
                useDB = false;
            }
        }
        AUTH_DAO = useDB ? new SqlAuthDAO() : new MemoryAuthDAO();
        USER_DAO = useDB ? new SqlUserDAO() : new MemoryUserDAO();
        GAME_DAO = useDB ? new SqlGameDAO() : new MemoryGameDAO();
    }


    public static AuthDAO getAuthDAO() {
        return AUTH_DAO;
    }

    public static UserDAO getUserDAO() {
        return USER_DAO;
    }

    public static GameDAO getGameDAO() { return GAME_DAO; }
}
