package dataaccess;

import model.*;

public interface AuthDAO {
    void clearAuthDB();

    RegisterResult addAuth(AuthData authData);

    boolean findAuth(String authToken);

    void removeAuth(String authToken);

    String findUsername(String authToken);
}
