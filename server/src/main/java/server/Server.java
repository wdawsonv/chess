package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import com.google.gson.Gson;
import service.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService service;

    public Server() {
            this(new UserService(new MySqlDataAccess()));
    }

    public Server(UserService service) {

        this.service = service;

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .delete("/db", this::clearAll);

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
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        } catch (BadPasswordException e) {
            ctx.status(400);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");
//            ctx.result(new Gson().toJson("Error: " + e.getMessage()));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");

        }
    }

    private void login(Context ctx) {
        LoginRequest user = new Gson().fromJson(ctx.body(), LoginRequest.class);
        try {
            LoginResult result = service.login(user);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (BadPasswordException | MissingUsernameException e) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        }
    }

    private void logout(Context ctx) {
        String token = ctx.header("authorization");

        try {
            LogoutResult result = service.logout(token);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");

        }
    }

    private void listGames(Context ctx) {
        String token = ctx.header("authorization");

        try {
            GameList gList = service.listGames(token);
            ctx.status(200);
            ctx.result(new Gson().toJson(gList));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        } catch (SQLException e) {
            ctx.status(500);
            ctx.json("error: placeholder: " + e.getMessage());
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");

        }
    }

    private void createGame(Context ctx) {
        CreateRequest request = new Gson().fromJson(ctx.body(), CreateRequest.class);
        String gameName = request.gameName();
        String token = ctx.header("authorization");

        try {
            CreateResult createdGame = service.createGame(gameName, token);
            ctx.status(200);
            ctx.result(new Gson().toJson(createdGame));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        } catch (AlreadyTakenException | BadRequestException e) {
            ctx.status(400);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");

        }
    }

    private void joinGame(Context ctx) {
        JoinRequest request = new Gson().fromJson(ctx.body(), JoinRequest.class);
        String color = request.playerColor();
        int gameID = request.gameID();
        String token = ctx.header("authorization");

        try {
            JoinResult joinedGame = service.joinGame(gameID, color, token);
            ctx.status(200);
            ctx.result(new Gson().toJson(joinedGame));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");

        }

    }

    private void clearAll(Context ctx) {
        try {
            service.clearAllData();
            ctx.result("{}");
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json("{ \"message\": \"Error: " + e.getMessage() + "\" }");
        }
    }



}
