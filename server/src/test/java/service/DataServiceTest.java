package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataServiceTest extends UnitTestUtils {
    @Test
    public void clearTest() {
        DataService.clearAllTables();
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(existingGame.gameID()));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(existingUser.username()));
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getUsernameFromToken(existingAuthToken));
    }
}
