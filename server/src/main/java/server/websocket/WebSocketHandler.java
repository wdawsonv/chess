package server.websocket;


import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws IOException {
        String json = ctx.message();
        UserGameCommand userGameCommand = new Gson().fromJson(json, UserGameCommand.class);

        System.out.println("Received: " + json);

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            handleConnectCommand(ctx, userGameCommand);
        }
    }

    private void handleConnectCommand(WsMessageContext ctx, UserGameCommand command) {
        connections.add(ctx.session);
        System.out.println("User joined game " + command.getGameID());
        ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        response.setGame(new Object());
        ctx.send(new Gson().toJson(response));

    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
        connections.remove(ctx.session);
    }
}
