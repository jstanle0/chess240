package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import static service.DataService.clearAllTables;

public class ClearDBHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) {
        clearAllTables();
    }
}
