package handler;

import com.google.gson.Gson;
import io.javalin.http.*;
import models.AuthData;
import models.LoginUserData;
import org.jetbrains.annotations.NotNull;

import static service.LoginService.login;

public class LoginHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws ForbiddenResponse, UnauthorizedResponse, BadRequestResponse {
        Gson gson = new Gson();
        LoginUserData data;
        try {
            data = gson.fromJson(context.body(), LoginUserData.class);
            if (data.username() == null || data.password() == null) {
                throw new Exception("missing username or password");
            }
        } catch (Exception e) {
            throw new BadRequestResponse("failed to parse body: " + e.getMessage());
        }

        AuthData authData = login(data);
        context.json(gson.toJson(authData));
    }
}
