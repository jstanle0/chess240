package dataaccess;

import models.UserData;

/**
 * DAO interface for the User table.
 * Fields:
 * Username: String, unique key
 * Password: String
 * Email: String
 */
public interface UserDAO {
    /**
     * Gets user data
     * @param username username
     * @return UserDataObject
     * @throws DataAccessException User does not exist
     */
    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData data);
    void clearTable();
}
