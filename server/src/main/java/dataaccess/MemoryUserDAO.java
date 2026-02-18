package dataaccess;

import models.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    HashMap<String, UserData> userTable = new HashMap<>();
    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = userTable.get(username);
        if (user == null) {
            throw new DataAccessException("user does not exist");
        }
        return user;
    }

    @Override
    public void createUser(UserData data) {
        userTable.put(data.username(), data);
    }
}
