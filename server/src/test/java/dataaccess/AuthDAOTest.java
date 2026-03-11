package dataaccess;

import models.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class AuthDAOTest {
    private static final AuthDAO AUTH_DAO = DAOs.getAuthDAO();

    @BeforeEach
    @AfterEach
    void clear() {
        AUTH_DAO.clearTable();
    }

    @Test
    void testClear() {
        var token = initializeDAO();
        AUTH_DAO.clearTable();
        Assertions.assertThrows(DataAccessException.class, () -> AUTH_DAO.getUsernameFromToken(token));
    }

    @Test
    void testCreateAuth() {
        var token = UUID.randomUUID();
        Assertions.assertDoesNotThrow(() -> AUTH_DAO.createAuth(new AuthData("a", token)));
    }

    @Test
    void testDeleteAuth() {
        var token = initializeDAO();
        Assertions.assertDoesNotThrow(() -> AUTH_DAO.deleteAuth(token));
        Assertions.assertThrows(DataAccessException.class, () -> AUTH_DAO.getUsernameFromToken(token));
    }

    @Test
    void testFailedDeleteAuth() {
        var randomToken = UUID.randomUUID();
        Assertions.assertThrows(DataAccessException.class, () -> AUTH_DAO.deleteAuth(randomToken));
    }

    @Test
    void testGetUsernameFromToken() {
        var token = initializeDAO();
        String username = Assertions.assertDoesNotThrow(() -> AUTH_DAO.getUsernameFromToken(token));
        Assertions.assertEquals("a", username);
    }

    @Test
    void testFailedGetUsernameFromToken() {
        var randomToken = UUID.randomUUID();
        Assertions.assertThrows(DataAccessException.class, () -> AUTH_DAO.getUsernameFromToken(randomToken));
    }

    private UUID initializeDAO() {
        var token = UUID.randomUUID();
        AUTH_DAO.createAuth(new AuthData("a", token));
        return token;
    }
}
