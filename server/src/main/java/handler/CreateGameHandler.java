package handler;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CreateGameHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws BadRequestResponse, UnauthorizedResponse {
        String authHeader = context.header("authorization");
        if (authHeader == null) {
            throw new UnauthorizedResponse("no token present");
        }
        UUID token = UUID.fromString(authHeader);

    }
}
