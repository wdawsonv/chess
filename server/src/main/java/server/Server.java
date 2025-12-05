package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import model.User;
import model.RegisterResult;
import com.google.gson.Gson;
import service.AlreadyTakenException;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserService service;

    public Server() {
        this(new UserService(new MemoryDataAccess()));
    }

    public Server(UserService service) {

        this.service = service;

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

    private void registerRequest(Context ctx) throws DataAccessException, AlreadyTakenException {
        User user = new Gson().fromJson(ctx.body(), User.class);
        try {
            RegisterResult result = service.register(user);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(new Gson().toJson("Error: " + e.getMessage()));
        }
    }

}
