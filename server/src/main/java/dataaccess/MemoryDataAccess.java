package dataaccess;

import model.*;
import java.util.HashMap;
import java.util.UUID;

public class MemoryDataAccess {

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
    final private HashMap<User, String> users = new HashMap<>();

    public User addUser(User user) {
        user = new User(user.username(), user.password(), user.email());
        users.put(user, "0");

        return user;
    }

    public String addAuth(User user) {
        String authToken = generateToken();
        user = new User(user.username(), user.password(), user.email());
        users.put(user, authToken);
        return authToken;
    }

    public void deleteUsers() {
        users.clear();
    }

    public HashMap<User, String> listUsers() {
        return users;
    }

    public User getUser(String username) {
        for (User user : users.keySet()) {
            if (user.username().equals(username)) {
                return user;
            }
        }

        return null;
    }
}
