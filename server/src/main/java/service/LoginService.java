package service;

import dataaccess.DataAccessException;
import handler.AuthData;
import models.UserData;

public class LoginService {
    public static AuthData register(UserData data) throws DataAccessException {
        return new AuthData("a", "a");
    }
}
