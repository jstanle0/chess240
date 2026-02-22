package dataaccess;

import models.AuthData;

import java.util.UUID;

/**
 * DAO interface for the Auth table.
 * Fields:
 * Username: String (unique key)
 * Current token: UUID
 */
public interface AuthDAO {
    /**
     * Gets a username from an auth token
     * @param token auth token taken from header
     * @return username as String
     * @throws DataAccessException token is invalid
     */
    String getUsernameFromToken(UUID token) throws DataAccessException;
    void createAuth(AuthData data);
    void deleteAuth(UUID token) throws DataAccessException;
    UUID getTokenFromUsername(String username) throws DataAccessException;
    void clearTable();
}
