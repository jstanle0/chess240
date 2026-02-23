package service;

import io.javalin.http.ForbiddenResponse;
import models.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
}
