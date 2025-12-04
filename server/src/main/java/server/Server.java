package server;

import io.javalin.*;
import io.javalin.http.Context;
import model.User;
import com.google.gson.Gson;
import service.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::registerRequest);

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void registerRequest(Context ctx) throws ResponseException {
        User user = new Gson().fromJson(ctx.body(), User.class);
        user = service.UserService.register(user);
        ctx.result(new Gson().toJson(user));
    }

}
