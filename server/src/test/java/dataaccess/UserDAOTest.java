package dataaccess;

import models.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDAOTest {
    private static final UserDAO USER_DAO = DAOs.getUserDAO();

    @BeforeEach
    @AfterEach
    void clear() {
        USER_DAO.clearTable();
    }

    @Test
    void testClearTable() {
        var data = initializeDAO();
        Assertions.assertDoesNotThrow(USER_DAO::clearTable);
        Assertions.assertThrows(DataAccessException.class, () -> USER_DAO.getUser(data.username()));
    }

    @Test
    void testCreateUser() {
        Assertions.assertDoesNotThrow(() -> USER_DAO.createUser(new UserData("a", "a", "a")));
    }

    @Test
    void testGetUser() {
        var data = initializeDAO();
        var receivedData = Assertions.assertDoesNotThrow(() -> USER_DAO.getUser(data.username()));
        Assertions.assertEquals(receivedData, data);
    }

    @Test
    void testFailedGetUser() {
        String fakeUser = "bob";
        Assertions.assertThrows(DataAccessException.class, () -> USER_DAO.getUser(fakeUser));
    }

    private UserData initializeDAO() {
        UserData data = new UserData("a", "a", "a");
        USER_DAO.createUser(data);
        return data;
    }
}
