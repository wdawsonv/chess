package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

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
import service.BadRequestException;
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
    public void handleMessage(WsMessageContext ctx) throws IOException, UnauthorizedException, DataAccessException, SQLException, BadRequestException {
        String json = ctx.message();
        UserGameCommand userGameCommand = new Gson().fromJson(json, UserGameCommand.class);

        System.out.println("Received: " + json);

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            handleConnectCommand(ctx, userGameCommand);
        }
    }

    private void handleConnectCommand(WsMessageContext ctx, UserGameCommand command) throws UnauthorizedException, DataAccessException, SQLException, IOException, BadRequestException {
        String token = command.getAuthToken();
        int gameID = command.getGameID();
        connections.add(gameID, ctx.session);
        ChessGame game = null;
        try {
            game = userService.getGame(gameID, token);
            if (game == null) {
                throw new NullPointerException();
            }
        } catch (Exception ex) {
            sendError(ctx, "Error: gameID not found" + gameID);
            return;
        }

        System.out.println("User joined game " + gameID);
        ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);

        response.setGame(game);

        ctx.send(new Gson().toJson(response));

        sendNotificationNotToMe(gameID, ctx.session, "A player has joined game " + gameID);

    }

    private void sendError(WsMessageContext ctx, String message) throws IOException {
        ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        error.setError(message);
        ctx.send(new Gson().toJson((error)));
    }

    private void sendNotificationNotToMe(int gameID, Session joiningSession, String text) throws IOException {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage(text);
        connections.broadcastExcept(gameID, joiningSession, new Gson().toJson(message));
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
        connections.remove(ctx.session);
    }
}
