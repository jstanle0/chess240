package dataaccess.memory;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import models.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<UUID, String> authTable = new HashMap<>();
    @Override
    public String getUsernameFromToken(UUID token) throws DataAccessException {
        String username = authTable.get(token);
        if (username == null) {
            throw new DataAccessException("no session associated with token");
        }
        return username;
    }

    @Override
    public void createAuth(AuthData data) {
        authTable.put(data.authToken(), data.username());
    }

    @Override
    public void deleteAuth(UUID token) throws DataAccessException {
        String username = authTable.remove(token);
        if (username == null) {
            throw new DataAccessException("session not found");
        }
    }

    @Override
    public void clearTable() {
        authTable.clear();
    }
}
