package server;

import io.javalin.*;
import handler.LoginHandler;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        createHandlers(javalin);
        createExceptionHandlers(javalin);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void createHandlers(Javalin javalin) {
        javalin.get("/health", ctx -> ctx.result("healthy :)"));
        javalin.post("/user", new LoginHandler());
    }

    private void createExceptionHandlers(Javalin javalin) {
        javalin.exception(BadRequestResponse.class, (e, ctx) -> {
            System.out.println("***Bad request exception: " + e.getMessage());
            ctx.status(400);
            ctx.json("{\"message\":\"bad request\"}");
        });
        javalin.exception(ForbiddenResponse.class, (e, ctx) -> {
            System.out.println("***Forbidden Action Exception: " + e.getMessage());
            ctx.status(403);
            ctx.json("{\"message\":\"already taken\"}");
        });
        javalin.exception(UnauthorizedResponse.class, (e, ctx) -> {
            System.out.println("***Unauthorized Exception: " + e.getMessage());
            ctx.status(401);
            ctx.json("{\"message\":\"unauthorized\"}");
        });
        javalin.exception(NotFoundResponse.class, (e, ctx) -> {
            System.out.println("***Not Found Exception: " + e.getMessage());
            ctx.status(404);
            ctx.json("{\"message\":\"not found\"}");
        });
        javalin.exception(Exception.class, (e, ctx) -> {
            System.out.println("***Exception: " + e.getMessage());
            ctx.status(500);
            ctx.json("{\"message\":\"internal server error: " + e.getMessage() + "\"}");
        });
    }
}
