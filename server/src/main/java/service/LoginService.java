package service;

import dataaccess.*;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;
import models.AuthData;
import models.LoginUserData;
import models.UserData;

import java.util.Objects;
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

    public static AuthData login(LoginUserData data) throws NotFoundResponse, UnauthorizedResponse {
        try {
            UserData userData = userDAO.getUser(data.username());
            if (!Objects.equals(userData.password(), data.password())) {
                throw new UnauthorizedResponse("incorrect password");
            }

            // Removes an old user session to prevent duplicate sessions
            try {
                UUID oldToken = authDAO.getTokenFromUsername(userData.username());
                authDAO.deleteAuth(oldToken);
            } catch (DataAccessException e) {
                // If either of these functions errors, it means the previous session doesn't exist
            }
            AuthData authData = new AuthData(data.username(), UUID.randomUUID());
            authDAO.createAuth(authData);
            return authData;
        } catch (DataAccessException e) {
            throw new NotFoundResponse(e.getMessage());
        }
    }

    public static void logout(UUID token) {
        authDAO.deleteAuth(token);
    }
}
