package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import model.RegisterResult;
import com.google.gson.Gson;
import service.AlreadyTakenException;
import service.BadPasswordException;
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
                .post("/user", this::register)
                .post("/session", this::loginRequest);

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) {
        UserData user = new Gson().fromJson(ctx.body(), UserData.class);
        try {
            RegisterResult result = service.register(user);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(new Gson().toJson("Error: " + e.getMessage()));
        } catch (DataAccessException e) {
            ctx.status(400);
            ctx.result(new Gson().toJson("Error: " + e.getMessage()));
        }
    }

    private void loginRequest(Context ctx) {
        LoginRequest user = new Gson().fromJson(ctx.body(), LoginRequest.class);
        try {
            LoginResult result = service.login(user);
        } catch (BadPasswordException e) {
            ctx.status(400);
            ctx.result(new Gson().toJson("Error: " + e.getMessage()));
        }
    }



}
