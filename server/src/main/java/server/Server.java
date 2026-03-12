package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.*;
import org.eclipse.jetty.server.Authentication;
import service.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", ctx -> {
            GameService.clear();
            AuthService.clear();
            UserService.clear();
        });

        javalin.post("/user", ctx -> {
            UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
            RegisterRequest registerRequest = new RegisterRequest(userData.username(), userData.password(), userData.email());
            UserService.register(registerRequest); //wont return anything
            RegisterResult registerResult = AuthService.createAuth(userData.username());
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
