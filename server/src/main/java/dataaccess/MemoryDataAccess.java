package dataaccess;

import model.*;
import java.util.HashMap;
import java.util.UUID;

public class MemoryDataAccess {

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
    final private HashMap<String, User> users = new HashMap<>();

    public User addUser(User user) {
        user = new User(user.username(), user.password(), user.email());
        users.put(generateToken(), user);

        return user;
    }

    public void deleteUsers() {
        users.clear();
    }

    public HashMap<String, User> listUsers() {
        return users;
    }
//    void insertUser(UserData u) throws DataAccessException {}
}
