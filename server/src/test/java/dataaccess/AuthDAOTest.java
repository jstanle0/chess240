package dataaccess;

import models.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class AuthDAOTest {
    private static final AuthDAO authDAO = DAOs.getAuthDAO();

    @BeforeEach
    @AfterEach
    void clear() {
        authDAO.clearTable();
    }

    @Test
    void testClear() {
        var token = initializeDAO();
        authDAO.clearTable();
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getUsernameFromToken(token));
    }

    @Test
    void testCreateAuth() {
        var token = UUID.randomUUID();
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(new AuthData("a", token)));
    }

    @Test
    void testDeleteAuth() {
        var token = initializeDAO();
        Assertions.assertDoesNotThrow(() -> authDAO.deleteAuth(token));
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getUsernameFromToken(token));
    }

    @Test
    void testFailedDeleteAuth() {
        var randomToken = UUID.randomUUID();
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.deleteAuth(randomToken));
    }

    @Test
    void testGetUsernameFromToken() {
        var token = initializeDAO();
        String username = Assertions.assertDoesNotThrow(() -> authDAO.getUsernameFromToken(token));
        Assertions.assertEquals("a", username);
    }

    @Test
    void testFailedGetUsernameFromToken() {
        var randomToken = UUID.randomUUID();
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getUsernameFromToken(randomToken));
    }

    private UUID initializeDAO() {
        var token = UUID.randomUUID();
        authDAO.createAuth(new AuthData("a", token));
        return token;
    }
}
