package dataaccess;

import model.*;

public interface UserDAO {
    void clearUserDB();

    boolean getUser(String username);

    void createUser(UserData userData);
}
