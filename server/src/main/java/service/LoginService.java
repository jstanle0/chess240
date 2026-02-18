package service;

import dataaccess.*;
import io.javalin.http.ForbiddenResponse;
import models.AuthData;
import models.UserData;

import java.util.UUID;

public class LoginService {
    static UserDAO userDAO = DAOs.getUserDAO();
    static AuthDAO authDAO = DAOs.getAuthDAO();
    public static AuthData register(UserData data) throws ForbiddenResponse {
        // Check if user exists
        try {
            userDAO.getUser(data.username());
            throw new ForbiddenResponse("tried to create an existing user");
        } catch (DataAccessException e) {
            //Ignore data access exceptions. This is the correct path
        }
        // Create new user
        userDAO.createUser(data);
        // Login the user
        AuthData authData = new AuthData(data.username(), UUID.randomUUID());
        authDAO.createAuth(authData);
        return authData;
    }
}
