package dataaccess;

import chess.ChessGame;
import model.*;
import service.AlreadyTakenException;
import service.BadRequestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MemoryDataAccess {

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
    final private List<UserData> users = new ArrayList<>();
    final private List<AuthData> auths = new ArrayList<>();
    final private List<GameData> games = new ArrayList<>();
    private int gameID = 1000;

    public UserData addUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.email());
        users.add(user);

        return user;
    }

    public String addAuth(String username) {
        String authToken = generateToken();
        AuthData userAuth = new AuthData(authToken, username);
        auths.add(userAuth);
        return authToken;
    }

    public void deleteUsers() {
        users.clear();
    }

    public List<UserData> listUsers() {
        return users;
    }

    //TEMP FOR TESTING
    public List<AuthData> listAuths() {
        return auths;
    }

    public UserData getUser(String username) {
        for (UserData user : users) {
            if (user.username().equals(username)) {
                return user;
            }
        }

        return null;
    }

    public AuthData getAuth(String token) {
        for (AuthData auth : auths) {
            if (auth.authToken().equals(token)) {
                return auth;
            }
        }

        return null;
    }

    public void removeAuth(AuthData authData) {
        auths.remove(authData);
    }

    public List<GameData> getGamesList() {
        return games;
    }

    public CreateResult createNewGame(String gameName) throws AlreadyTakenException {
        for (GameData game : games) {
            if (game.gameName().equals(gameName)) {
                throw new AlreadyTakenException("game name already in use, please choose new name");
            }
        }
        gameID++;
        games.add(new GameData(gameID, null, null, gameName, new ChessGame()));
        return new CreateResult(gameID);
    }

    public void clearUserData() {
        users.clear();
    }

    public void clearAuthData() {
        auths.clear();
    }

    public void clearGameData() {
        games.clear();
    }

    public JoinResult joinExistingGame(int gameID, String color, String username) throws DataAccessException, AlreadyTakenException, BadRequestException {
        boolean gameFound = false;
        GameData requestedGame = new GameData(0, "", "", "", new ChessGame());

        for (GameData game : games) {
            if (game.gameID() == gameID) {
                requestedGame = game;
                gameFound = true;
            }
        }

        if (!gameFound) {
            throw new DataAccessException("error: game ID not found");
        } if (color == null) {
            throw new BadRequestException("no color provided");
        } else if (color.equals("WHITE") && !(requestedGame.whiteUsername() == null)) {
            throw new AlreadyTakenException("error: there is already a player on white team");
        } else if (color.equals("BLACK") && !(requestedGame.blackUsername() == null)) {
            throw new AlreadyTakenException("error: there is already a player on black team");
        } else if (color.equals("WHITE")) {

            GameData requestedGameNewUser = new GameData(requestedGame.gameID(), username, requestedGame.blackUsername(), requestedGame.gameName(), requestedGame.game());
            for (int i = 0; i < games.size(); i++) {
                if (games.get(i).gameID() == gameID) {
                    games.set(i, requestedGameNewUser);
                    break;
                }
            }
        } else if (color.equals("BLACK")) {

            GameData requestedGameNewUser = new GameData(requestedGame.gameID(), requestedGame.whiteUsername(), username, requestedGame.gameName(), requestedGame.game());
            for (int i = 0; i < games.size(); i++) {
                if (games.get(i).gameID() == gameID) {
                    games.set(i, requestedGameNewUser);
                    break;
                }
            }
        } else {
            throw new BadRequestException("invalid color provided");
        }

        return new JoinResult();
    }

}
