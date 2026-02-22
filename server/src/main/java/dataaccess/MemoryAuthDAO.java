package dataaccess;

import models.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
    public void deleteAuth(UUID token) {
        authTable.remove(token);
    }

    @Override
    public UUID getTokenFromUsername(String username) throws DataAccessException {
        for (Map.Entry<UUID, String> entry : authTable.entrySet()) {
            if (Objects.equals(entry.getValue(), username)) {
                return entry.getKey();
            }
        }
        throw new DataAccessException("user doesn't have active session");
    }

    @Override
    public void clearTable() {
        authTable.clear();
    }
}
