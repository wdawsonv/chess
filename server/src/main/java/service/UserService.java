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

    public User addUser(User user) throws DataAccessException {
        return memoryDataAccess.addUser(user);
    }

    public void deleteUsers() throws DataAccessException {
        memoryDataAccess.deleteUsers();
    }

    public HashMap<String, User> listUsers() throws DataAccessException {
        return memoryDataAccess.listUsers();
    }
}