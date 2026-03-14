package dataaccess;

import model.*;

public interface UserDAO {
    void clearUserDB() throws DataAccessException;

    boolean getUser(String username) throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    UserData getUserData(String username) throws UnauthorizedException, DataAccessException;
}
