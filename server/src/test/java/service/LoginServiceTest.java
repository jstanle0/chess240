package service;

import dataaccess.DataAccessException;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import models.AuthData;
import models.LoginUserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class LoginServiceTest extends UnitTestUtils {
    @Test
    public void registerTest() {
        AuthData newAuth = LoginService.register(newUser);
        Assertions.assertNotEquals(newAuth.authToken(), existingAuthToken);
        Assertions.assertDoesNotThrow(() -> userDAO.getUser(newUser.username()));
    }

    @Test
    public void identicalRegisterTest() {
        Assertions.assertThrows(ForbiddenResponse.class, () -> LoginService.register(existingUser));
    }

    @Test
    public void loginTest() {
        LoginUserData loginData = new LoginUserData(existingUser.username(), existingUser.password());
        AuthData authData = LoginService.login(loginData);
        String username = Assertions.assertDoesNotThrow(() -> authDAO.getUsernameFromToken(authData.authToken()));
        Assertions.assertEquals(username, existingUser.username());
    }

    @Test
    public void invalidLoginTest() {
        LoginUserData badLoginData = new LoginUserData(existingUser.username(), "fakePassword");
        Assertions.assertThrows(UnauthorizedResponse.class, () -> LoginService.login(badLoginData));
    }

    @Test
    public void logoutTest() {
        Assertions.assertDoesNotThrow(() -> LoginService.logout(existingAuthToken));
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getUsernameFromToken(existingAuthToken));
    }

    @Test
    public void invalidLogoutTest() {
        Assertions.assertThrows(UnauthorizedResponse.class, () -> LoginService.logout(UUID.randomUUID()));
    }

    @Test
    public void verifyAuthTest() {
        Assertions.assertDoesNotThrow(() -> LoginService.verifyAuth(existingAuthToken));
        Assertions.assertThrows(UnauthorizedResponse.class, () -> LoginService.verifyAuth(UUID.randomUUID()));
    }
}
