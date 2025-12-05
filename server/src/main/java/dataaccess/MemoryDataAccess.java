package dataaccess;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MemoryDataAccess {

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
    final private List<UserData> users = new ArrayList<>();
    final private List<AuthData> auths = new ArrayList<>();
//    final private HashMap<UserData, String> users = new HashMap<>();

    public UserData addUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.email());
        users.add(user);

        return user;
    }

    public String addAuth(UserData user) {
        String authToken = generateToken();
        String username = user.username();
        AuthData userAuth = new AuthData(authToken, username);
        auths.add(userAuth);
        return authToken;
    }

    public void deleteUsers() {
        users.clear();
    }

    public List<UserData> listUsers() {
        return users;
    }

    public UserData getUser(String username) {
        for (UserData user : users) {
            if (user.username().equals(username)) {
                return user;
            }
        }

        return null;
    }
}
