package handler;

import com.google.gson.Gson;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import models.CreateGameBody;
import org.jetbrains.annotations.NotNull;

import static service.GameService.createGame;

public class CreateGameHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws BadRequestResponse {
        Gson gson = new Gson();
        CreateGameBody data;
        try {
            data = gson.fromJson(context.body(), CreateGameBody.class);
        } catch (Exception e) {
            throw new BadRequestResponse("failed to parse body: " + e.getMessage());
        }
        context.json(gson.toJson(createGame(data.gameName())));
    }
}
