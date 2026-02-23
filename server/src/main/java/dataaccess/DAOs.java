package dataaccess;

/**
 * Class that globally instantiates and serves DAOs
 */
public class DAOs {
    private static final AuthDAO AUTH_DAO = new MemoryAuthDAO();
    private static final UserDAO USER_DAO = new MemoryUserDAO();
    private static final GameDAO GAME_DAO = new MemoryGameDAO();

    public static AuthDAO getAuthDAO() {
        return AUTH_DAO;
    }

    public static UserDAO getUserDAO() {
        return USER_DAO;
    }

    public static GameDAO getGameDAO() { return GAME_DAO; }
}
