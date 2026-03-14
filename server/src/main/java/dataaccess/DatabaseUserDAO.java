package dataaccess;

import model.UserData;

public class DatabaseUserDAO implements UserDAO {
    @Override
    public void clearUserDB() {

    }

    @Override
    public boolean getUser(String username) {
        return false;
    }

    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public UserData getUserData(String username) throws UnauthorizedException {
        return null;
    }
}
