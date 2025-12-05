package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import model.User;
import com.google.gson.Gson;
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

    private void registerRequest(Context ctx) throws DataAccessException {
        User user = new Gson().fromJson(ctx.body(), User.class);
        user = service.addUser(user);
        ctx.result(new Gson().toJson(user));
    }

}
