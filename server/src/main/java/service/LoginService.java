package service;

import dataaccess.*;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import models.AuthData;
import models.LoginUserData;
import models.UserData;
import org.mindrot.jbcrypt.BCrypt;

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
        var hashedData = new UserData(data.username(), BCrypt.hashpw(data.password(), BCrypt.gensalt()), data.email());
        // Create new user
        userDAO.createUser(hashedData);
        // Login the user
        AuthData authData = new AuthData(data.username(), UUID.randomUUID());
        authDAO.createAuth(authData);
        return authData;
    }

    public static AuthData login(LoginUserData data) throws UnauthorizedResponse {
        try {
            UserData userData = userDAO.getUser(data.username());
            try {
                if (!BCrypt.checkpw(data.password(), userData.password())) {
                    throw new UnauthorizedResponse("incorrect password");
                }
            } catch (Exception e) {
                throw new UnauthorizedResponse("password failed authentication");
            }

            AuthData authData = new AuthData(data.username(), UUID.randomUUID());
            authDAO.createAuth(authData);
            return authData;
        } catch (DataAccessException e) {
            throw new UnauthorizedResponse(e.getMessage());
        }
    }

    public static void logout(UUID token) throws UnauthorizedResponse {
        try {
            authDAO.deleteAuth(token);
        } catch (DataAccessException e) {
            throw new UnauthorizedResponse(e.getMessage());
        }
    }

    public static void verifyAuth(UUID token) throws UnauthorizedResponse {
        try {
            authDAO.getUsernameFromToken(token);
        } catch (DataAccessException e) {
            throw new UnauthorizedResponse(e.getMessage());
        }
    }
}
