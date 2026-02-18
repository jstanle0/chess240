package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import models.AuthData;
import models.UserData;
import org.jetbrains.annotations.NotNull;

import static service.LoginService.register;

public class LoginHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) {
        UserData body;
        try {
            body = new Gson().fromJson(ctx.body(), UserData.class);
        } catch (Exception e) {
            throw new BadRequestResponse(e.getMessage());
        }

        AuthData authData = register(body);

        ctx.json(new Gson().toJson(authData));
    }
}
