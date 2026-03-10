package dataaccess;

import models.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDAOTest {
    private static final UserDAO userDAO = DAOs.getUserDAO();

    @BeforeEach
    @AfterEach
    void clear() {
        userDAO.clearTable();
    }

    @Test
    void testClearTable() {
        var data = initializeDAO();
        Assertions.assertDoesNotThrow(userDAO::clearTable);
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(data.username()));
    }

    @Test
    void testCreateUser() {
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(new UserData("a", "a", "a")));
    }

    @Test
    void testGetUser() {
        var data = initializeDAO();
        var receivedData = Assertions.assertDoesNotThrow(() -> userDAO.getUser(data.username()));
        Assertions.assertEquals(receivedData, data);
    }

    @Test
    void testFailedGetUser() {
        String fakeUser = "bob";
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(fakeUser));
    }

    private UserData initializeDAO() {
        UserData data = new UserData("a", "a", "a");
        userDAO.createUser(data);
        return data;
    }
}
