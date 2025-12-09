package service;

import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;

import io.javalin.http.UnauthorizedResponse;
import model.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class UserService {

//    private final mySqlDataAccess mySqlDataAccess;
    private final MySqlDataAccess mySqlDataAccess;

    public UserService(MySqlDataAccess mySqlDataAccess) {
        this.mySqlDataAccess = mySqlDataAccess;

    }

    public RegisterResult register(UserData user) throws DataAccessException, AlreadyTakenException, BadPasswordException {
        if (mySqlDataAccess.getUser(user.username()) == null) {
            if (user.password() == null) {
                throw new BadPasswordException("must provide a password");
            }
            user = createUser(user);
            String authToken = createAuth(user.username());
            return new RegisterResult(user.username(), authToken);
        } else {
            throw new AlreadyTakenException("username already taken");
        }
    }

    public LoginResult login(LoginRequest user) throws BadPasswordException, MissingUsernameException, DataAccessException, BadRequestException {
        String givenUsername = user.username();
        String givenPassword = user.password();

        if (user.username() == null || user.password() == null) {
            throw new BadRequestException("no username/password provided");
        }

        if (mySqlDataAccess.getUser(user.username()) == null) {
            throw new MissingUsernameException("username not in database");
        } else {
            UserData actualUser = getUser(givenUsername);
            String actualPassword = actualUser.password();

            if (givenPassword.equals(actualPassword)) {
                String authToken = createAuth(givenUsername);
                return new LoginResult(givenUsername, authToken);
            } else {
                throw new BadPasswordException("password is incorrect");
            }
        }
    }

    public LogoutResult logout(String token) throws UnauthorizedException, DataAccessException {

        if (getAuth(token) == null) {
            throw new UnauthorizedException("unauthorized");
        } else {
            AuthData auth = getAuth(token);
            removeAuth(auth);
            return new LogoutResult();
        }
        //find Auth by authdata, if nothing returns throw unauthorized error
        //if authdata does return then remove authdata from the database and return logout result :P

    }

    public List<GameData> listGames(String token) throws UnauthorizedException, DataAccessException, SQLException {

        if (getAuth(token) == null) {
            throw new UnauthorizedException("unauthorized");
        } else {
            List<GameData> list = getGamesList();
            return list;
        }
    }

    public CreateResult createGame(String gameName, String token) throws UnauthorizedException, AlreadyTakenException, BadRequestException, DataAccessException {

        if (gameName == null) {
            throw new BadRequestException("no gamename included");
        }
        if (getAuth(token) == null) {
            throw new UnauthorizedException("unauthorized");
        } else {
            return createNewGame(gameName);
        }
    }

    private CreateResult createNewGame(String gameName) throws DataAccessException {
        return mySqlDataAccess.createNewGame(gameName);
    }

    public UserData createUser(UserData user) throws DataAccessException {
        return mySqlDataAccess.addUser(user);
    }

    private String createAuth(String username) throws DataAccessException {
        return mySqlDataAccess.addAuth(username);
    }

//    public void deleteUsers() throws DataAccessException {
//        mySqlDataAccess.deleteUsers();
//    }

    public List<UserData> listUsers() throws DataAccessException, SQLException {
        return mySqlDataAccess.listUsers();
    }

    public UserData getUser(String username) throws DataAccessException {
        return mySqlDataAccess.getUser(username);
    }

    private AuthData getAuth(String token) throws DataAccessException {
        return mySqlDataAccess.getAuth(token);
    }

    private void removeAuth(AuthData authData) throws DataAccessException {
        mySqlDataAccess.removeAuth(authData);
    }

    private List<GameData> getGamesList() throws DataAccessException, SQLException {
        return mySqlDataAccess.getGamesList();
    }

    public void clearAllData() throws DataAccessException {
        mySqlDataAccess.clearUserData();
        mySqlDataAccess.clearGameData();
        mySqlDataAccess.clearAuthData();
    }

    public JoinResult joinGame(int gameID, String color, String authToken) throws UnauthorizedException, AlreadyTakenException, DataAccessException, BadRequestException {

        if (getAuth(authToken) == null) {
            throw new UnauthorizedException("unauthorized");
        } else {
            AuthData userAuthData = mySqlDataAccess.getAuth(authToken);
            String username = userAuthData.username();

            return joinExistingGame(gameID, color, username);
        }
    }

    private JoinResult joinExistingGame(int gameID, String color, String username) throws AlreadyTakenException, DataAccessException, BadRequestException {
        return mySqlDataAccess.joinExistingGame(gameID, color, username);
    }
}