package dataaccess;

import model.*;

import java.util.ArrayList;


public class MemoryUserDAO implements UserDAO {
    private static ArrayList<UserData> users = new ArrayList<>();

    @Override
    public void clearUserDB() {
        users.clear();
    }

    @Override
    public boolean getUser(String username) {
        for (UserData user : users) {
            if (user.username().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void createUser(UserData userData) {
        users.add(userData);
    }

    @Override
    public UserData getUserData(String username) throws UnauthorizedException {
        for (UserData user : users) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new UnauthorizedException("unauthorized");
    }
}
