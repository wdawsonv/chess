package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.*;
import org.eclipse.jetty.server.Authentication;
import service.*;
import dataaccess.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.exception(DataAccessException.class, this::dataAccessExceptionHandler);
        javalin.exception(AlreadyTakenException.class, this::alreadyTakenExceptionHandler);
        javalin.exception(BadRequestException.class, this::badRequestExceptionHandler);

        javalin.delete("/db", ctx -> {
            GameService.clear();
            AuthService.clear();
            UserService.clear();
        });

        javalin.post("/user", ctx -> {
            UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
            RegisterRequest registerRequest = new RegisterRequest(userData.username(), userData.password(), userData.email());

            UserService.register(registerRequest);
            RegisterResult registerResult = AuthService.createAuth(userData.username());
            ctx.status(200);
            ctx.result(new Gson().toJson(registerResult));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void badRequestExceptionHandler(Exception e, Context ctx) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        ctx.status(400);
        ctx.json(body);
    }

    private void alreadyTakenExceptionHandler(Exception e, Context ctx) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        ctx.status(403);
        ctx.json(body);
    }

    private void dataAccessExceptionHandler(Exception e, Context ctx) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        ctx.status(500);
        ctx.json(body);
    }
}
