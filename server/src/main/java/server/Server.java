package server;

import dataaccess.DataAccessException;
import io.javalin.*;
import handler.LoginHandler;
import io.javalin.http.BadRequestResponse;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        createHandlers(javalin);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void createHandlers(Javalin javalin) {
        javalin.get("/health", ctx -> {
            ctx.result("healthy :)");
        });
        javalin.post("/user", new LoginHandler());

        javalin.exception(BadRequestResponse.class, (e, ctx) -> {
            System.out.println("***Bad request exception: " + e.getMessage());
            ctx.status(400);
            ctx.json("{\"message\":\"Invalid body\"}");
        });
    }
}
