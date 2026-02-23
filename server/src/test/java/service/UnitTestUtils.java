package service;

import dataaccess.AuthDAO;
import dataaccess.DAOs;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import models.AuthData;
import models.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

public abstract class UnitTestUtils {
    protected static AuthDAO authDAO = DAOs.getAuthDAO();
    protected static UserDAO userDAO = DAOs.getUserDAO();
    protected static GameDAO gameDAO = DAOs.getGameDAO();

    protected static UserData existingUser = new UserData("user1", "secure", "a@a.a");
    protected static UserData newUser = new UserData("user2", "verySecure", "b@b.b");
    protected static UUID existingAuthToken;

    /**
     * setup pre-populates some data into the different DAOs.
     * Technically these aren't pure unit tests because they don't stub the DAOs.
     */
    @BeforeEach
    public void setup() {
        userDAO.createUser(existingUser);
        UUID token = UUID.randomUUID();
        authDAO.createAuth(new AuthData(existingUser.username(), token));
        existingAuthToken = token;
        gameDAO.createGame("new game");
    }

    @AfterEach
    public void clear() {
        userDAO.clearTable();
        authDAO.clearTable();
        gameDAO.clearTable();
    }
}
