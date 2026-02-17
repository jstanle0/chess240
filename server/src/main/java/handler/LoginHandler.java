package handler;

import com.google.gson.Gson;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.validation.ValidationException;
import models.UserData;
import org.jetbrains.annotations.NotNull;

public class LoginHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) {
        UserData body;
        try {
            String json = ctx.body();
            body = new Gson().fromJson(json, UserData.class);

        } catch (Exception e) {
//
//            return;
            throw new BadRequestResponse();
        }

        ctx.json("{\"hello\": \"world\"}");
    }
}
