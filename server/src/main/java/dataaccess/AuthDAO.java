package dataaccess;

import model.*;

public interface AuthDAO {
    void clearAuthDB() throws DataAccessException;

    RegisterResult addAuth(AuthData authData) throws DataAccessException;

    boolean findAuth(String authToken) throws DataAccessException;

    void removeAuth(String authToken) throws DataAccessException;

    String findUsername(String authToken) throws DataAccessException;
}
