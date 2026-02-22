package handler;

import com.google.gson.Gson;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import models.JoinGameBody;
import org.jetbrains.annotations.NotNull;

import static service.GameService.joinGame;

public class JoinGameHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws BadRequestResponse, ForbiddenResponse {
        Gson gson = new Gson();
        JoinGameBody data;
        try {
            data = gson.fromJson(context.body(), JoinGameBody.class);
            if (data.playerColor() == null || data.gameID() == null) {
                throw new Exception("missing necessary information");
            }
        } catch (Exception e) {
            throw new BadRequestResponse("failed to parse body: " + e.getMessage());
        }
        joinGame(data, context.header("authorization"));
    }
}
