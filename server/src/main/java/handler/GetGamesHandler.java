package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import static service.GameService.getGames;

public class GetGamesHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) {
        context.json(new Gson().toJson(getGames()));
    }
}
