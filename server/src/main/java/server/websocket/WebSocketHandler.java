package server.websocket;

import chess.ChessMove;
import chess.InvalidMoveException;
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
    public void handleMessage(WsMessageContext ctx) throws IOException, UnauthorizedException, DataAccessException, SQLException, BadRequestException, InvalidMoveException {
        String json = ctx.message();
        UserGameCommand userGameCommand = new Gson().fromJson(json, UserGameCommand.class);

        System.out.println("Received: " + json);

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            handleConnectCommand(ctx, userGameCommand);
        } else if (userGameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            ChessMove move = userGameCommand.getMove();
            handleMakeMove(ctx, userGameCommand, move);
        } else if (userGameCommand.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            handleResign(ctx, userGameCommand);
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

    private void handleMakeMove(WsMessageContext ctx, UserGameCommand command, ChessMove move) throws UnauthorizedException, BadRequestException, DataAccessException, InvalidMoveException, IOException {
        String token = command.getAuthToken();
        int gameID = command.getGameID();

        ChessGame game = null;
        try {
            game = userService.getGame(gameID, token);
        } catch (UnauthorizedException ex) {
            sendError(ctx, "unauthorized: " + ex.getMessage());
        }

        if (game.isResigned()) {
            sendError(ctx, "Game is over stop trying to move 4head");
            return;
        }
        System.out.println("Turn before move: " + game.getTeamTurn());

        ChessGame.TeamColor userColor = userService.getPlayerColor(gameID, token);
        ChessGame.TeamColor pieceColor = game.getBoard().getPiece(move.getStartPosition()).getTeamColor();

        if (userColor != pieceColor || userColor == null) { //this is equals because piececolor has been updated but usercolor hasn't yet wahoo
            sendError(ctx, "Error: don't move someone else's piece lol");
            return;
        }
        try {
            game.makeMove(move);
        } catch (InvalidMoveException ex) {
            sendError(ctx, "Invalid move: " + ex.getMessage());
            return;
        }
        userService.saveGame(gameID, game, token);
        sendLoadGame(ctx, game);
        sendNotificationNotToMe(gameID, ctx.session, "Move made: " + move.toString());
        broadcastUpdatedBoardToOthers(gameID, ctx.session, game);
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand command) throws UnauthorizedException, BadRequestException, DataAccessException, IOException {
        String token = command.getAuthToken();
        int gameID = command.getGameID();

        ChessGame.TeamColor resignerColor = userService.getPlayerColor(gameID, token);
        ChessGame game = userService.getGame(gameID, token);

        if (game.isResigned()) {
            sendError(ctx, "don't resign slime you already won!");
            return;
        }

        if (resignerColor == null) {
            sendError(ctx, "Error: spectators can't resign");
            return;
        }

        game.resign();
        userService.saveGame(gameID, game, token);

        String message = resignerColor + " team resigns";
        ServerMessage resignation = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        resignation.setMessage(message);
        connections.broadcast(gameID, new Gson().toJson(resignation));
    }

    private void sendLoadGame(WsMessageContext ctx, ChessGame game) {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        message.setGame(game);
        ctx.send(new Gson().toJson(message));
    }

    private void broadcastUpdatedBoardToOthers(int gameID, Session session, ChessGame game) {
        ServerMessage update = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        update.setGame(game);
        connections.broadcastExcept(gameID, session, new Gson().toJson(update));
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
