package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static service.LoginService.verifyAuth;

public class VerifyAuthMiddleware implements Handler {
    @Override
    public void handle(@NotNull Context context) throws UnauthorizedResponse {
        String authHeader = context.header("authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            throw new UnauthorizedResponse("no token present");
        }
        UUID token = UUID.fromString(authHeader);
        verifyAuth(token);
    }
}
