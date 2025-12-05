package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;

public class UserService {

    private final MemoryDataAccess memoryDataAccess;

    public UserService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }

    public User addUser(User user) throws DataAccessException {
        return memoryDataAccess.addUser(user);
    }
}