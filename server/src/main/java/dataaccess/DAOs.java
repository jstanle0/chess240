package dataaccess;

/**
 * Class that globally instantiates and serves DAOs
 */
public class DAOs {
    private static final AuthDAO authDAO = new MemoryAuthDAO();
    private static final UserDAO userDAO = new MemoryUserDAO();
    private static final GameDAO gameDAO = new MemoryGameDAO();

    public static AuthDAO getAuthDAO() {
        return authDAO;
    }

    public static UserDAO getUserDAO() {
        return userDAO;
    }

    public static GameDAO getGameDAO() { return gameDAO; }
}
