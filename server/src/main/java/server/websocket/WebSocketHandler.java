package server.websocket;


import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import model.GameList;
import service.UnauthorizedException;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService;

    public WebSocketHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws IOException, UnauthorizedException, DataAccessException, SQLException {
        String json = ctx.message();
        UserGameCommand userGameCommand = new Gson().fromJson(json, UserGameCommand.class);

        System.out.println("Received: " + json);

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            handleConnectCommand(ctx, userGameCommand);
        }
    }

    private void handleConnectCommand(WsMessageContext ctx, UserGameCommand command) throws UnauthorizedException, DataAccessException, SQLException {
        connections.add(ctx.session);
        String token = command.getAuthToken();
        int gameID = command.getGameID();
        System.out.println("User joined game " + gameID);
        ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        GameList allGames = userService.listGames(token);

        ChessGame game = new ChessGame();
        for (GameData gameData : allGames) {
            if (gameData.gameID() == gameID) {
                game = gameData.game();
            }
        }

        response.setGame(game);

        ctx.send(new Gson().toJson(response));

    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
        connections.remove(ctx.session);
    }
}
