package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.*;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.util.log.Log;
import service.*;
import dataaccess.*;

import java.util.ArrayList;
import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.exception(DataAccessException.class, this::dataAccessExceptionHandler);
        javalin.exception(AlreadyTakenException.class, this::alreadyTakenExceptionHandler);
        javalin.exception(BadRequestException.class, this::badRequestExceptionHandler);
        javalin.exception(UnauthorizedException.class, this::unauthorizedExceptionHandler);

        javalin.delete("/db", ctx -> {
            AuthService.clear();
            UserService.clear();
            GameService.clear();

            ctx.status(200);
            ctx.result();
        });

        javalin.post("/user", ctx -> {
            UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
            RegisterRequest registerRequest = new RegisterRequest(userData.username(), userData.password(), userData.email());

            UserService.register(registerRequest);
            RegisterResult registerResult = AuthService.createAuth(userData.username());
            ctx.status(200);
            ctx.result(new Gson().toJson(registerResult));
        });

        javalin.post("/session", ctx -> {
            LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);

            //this line should throw all the errors i thinnkkkkk
            UserService.checkLogin(loginRequest);

            RegisterResult registerResult = AuthService.createAuth(loginRequest.username());
            //not too fond of this but it's the cleanest route i could think of
            LoginResult loginResult = new LoginResult(registerResult.username(), registerResult.authToken());
            ctx.status(200);
            ctx.result(new Gson().toJson(loginResult));
        });

        javalin.delete("/session", ctx -> {
            String authToken = ctx.header("authorization");
            LogoutRequest logoutRequest = new LogoutRequest(authToken);

            AuthService.logout(logoutRequest);
            ctx.status(200);
        });

        javalin.post("/game", ctx -> {
            String authToken = ctx.header("authorization");
            AuthService.findAuth(authToken);

            CreateGameRequest createGameRequest = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameResult createGameResult = GameService.createGame(createGameRequest);

            ctx.status(200);
            ctx.result(new Gson().toJson(createGameResult));
        });

        javalin.get("/game", ctx -> {
            String authToken = ctx.header("authorization");
            AuthService.findAuth(authToken);

            ArrayList<GameData> games = GameService.listGames();

            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of("games", games)));
        });

        javalin.put("/game", ctx -> {
            String authToken = ctx.header("authorization");
            AuthService.findAuth(authToken);

            String username = AuthService.findAuthReturnUsername(authToken);

            JoinGameRequest joinGameRequest = new Gson().fromJson(ctx.body(), JoinGameRequest.class);

            GameService.joinGame(joinGameRequest, username);

            ctx.status(200);
            ctx.result();
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

    private void unauthorizedExceptionHandler(Exception e, Context ctx) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        ctx.status(401);
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
