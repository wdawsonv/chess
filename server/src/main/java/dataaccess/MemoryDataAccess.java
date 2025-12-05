package dataaccess;

import model.*;
import java.util.HashMap;

public class MemoryDataAccess {

    private int nextId = 1;
    final private HashMap<Integer, User> users = new HashMap<>();

    public User addUser(User user) {
        user = new User(user.username(), user.password(), user.email());
        users.put(nextId++, user);

        return user;
    }

    public void deleteUsers() {
        users.clear();
    }

    public HashMap<Integer, User> listUsers() {
        return users;
    }
//    void insertUser(UserData u) throws DataAccessException {}
}
