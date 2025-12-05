package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;

import model.*;

import java.util.HashMap;

public class UserService {

    private final MemoryDataAccess memoryDataAccess;

    public UserService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }

    public RegisterResult register(User user) throws DataAccessException, AlreadyTakenException {
        if (memoryDataAccess.getUser(user.username()) == null) {
            user = createUser(user);
            String authToken = createAuth(user);
            return new RegisterResult(user.username(), authToken);
        } else {
            throw new AlreadyTakenException("username already taken");
        }
    }

    public User createUser(User user) throws DataAccessException {
        return memoryDataAccess.addUser(user);
    }

    private String createAuth(User user) throws DataAccessException {
        return memoryDataAccess.addAuth(user);
    }

    public void deleteUsers() throws DataAccessException {
        memoryDataAccess.deleteUsers();
    }

    public HashMap<User, String> listUsers() throws DataAccessException {
        return memoryDataAccess.listUsers();
    }

    public User getUser(String username) {
        return memoryDataAccess.getUser(username);
    }
}