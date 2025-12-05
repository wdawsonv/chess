package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;

import model.*;

import java.util.HashMap;
import java.util.List;

public class UserService {

    private final MemoryDataAccess memoryDataAccess;

    public UserService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }

    public RegisterResult register(UserData user) throws DataAccessException, AlreadyTakenException {
        if (memoryDataAccess.getUser(user.username()) == null) {
            user = createUser(user);
            String authToken = createAuth(user.username());
            return new RegisterResult(user.username(), authToken);
        } else {
            throw new AlreadyTakenException("username already taken");
        }
    }

    public LoginResult login(LoginRequest user) throws BadPasswordException, MissingUsernameException, DataAccessException {
        String givenUsername = user.username();
        String givenPassword = user.password();

        if (memoryDataAccess.getUser(user.username()) == null) {
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

    public LogoutResult logout(String token) throws UnauthorizedException {

        if (getAuth(token) == null) {
            throw new UnauthorizedException("error: unauthorized");
        } else {
            AuthData auth = getAuth(token);
            removeAuth(auth);
            return new LogoutResult();
        }
        //find Auth by authdata, if nothing returns throw unauthorized error
        //if authdata does return then remove authdata from the database and return logout result :P

    }

    public UserData createUser(UserData user) throws DataAccessException {
        return memoryDataAccess.addUser(user);
    }

    private String createAuth(String username) throws DataAccessException {
        return memoryDataAccess.addAuth(username);
    }

    public void deleteUsers() throws DataAccessException {
        memoryDataAccess.deleteUsers();
    }

    public List<UserData> listUsers() throws DataAccessException {
        return memoryDataAccess.listUsers();
    }

    public UserData getUser(String username) {
        return memoryDataAccess.getUser(username);
    }

    private AuthData getAuth(String token) {
        return memoryDataAccess.getAuth(token);
    }

    private void removeAuth(AuthData authData) {
        memoryDataAccess.removeAuth(authData);
    }
}