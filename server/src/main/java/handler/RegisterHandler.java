package handler;

import com.google.gson.Gson;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import models.AuthData;
import models.UserData;
import org.jetbrains.annotations.NotNull;

import static service.LoginService.register;

public class RegisterHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws BadRequestResponse, ForbiddenResponse {
        Gson gson = new Gson();
        UserData body;
        try {
            body = gson.fromJson(ctx.body(), UserData.class);
            if (body.username() == null || body.password() == null || body.email() == null) {
                throw new Exception("missing necessary field");
            }
        } catch (Exception e) {
            throw new BadRequestResponse(e.getMessage());
        }

        AuthData authData = register(body);

        ctx.json(gson.toJson(authData));
    }
}
