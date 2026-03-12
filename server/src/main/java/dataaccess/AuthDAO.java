package dataaccess;

import model.*;

public interface AuthDAO {
    void clearAuthDB();

    RegisterResult addAuth(AuthData authData);
}
