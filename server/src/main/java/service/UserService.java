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
            String authToken = createAuth(user);
            return new RegisterResult(user.username(), authToken);
        } else {
            throw new AlreadyTakenException("username already taken");
        }
    }

    public LoginResult login(LoginRequest user) throws BadPasswordException {
        String givenUsername = user.username();
        String givenPassword = user.password();

        if (memoryDataAccess.getUser(user.username()) == null) {
            throw new MissingUsernameException("username not in database");
        } else {
            UserData actualUser = getUser(givenUsername);
            String actualPassword = actualUser.password();
            if (givenPassword.equals(actualPassword)) {
                String authToken = createAuth(user);
                return new LoginResult(givenUsername, authToken);
            }
        }
    }

    public UserData createUser(UserData user) throws DataAccessException {
        return memoryDataAccess.addUser(user);
    }

    private String createAuth(UserData user) throws DataAccessException {
        return memoryDataAccess.addAuth(user);
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
}