package dataaccess;

import model.*;

import java.util.ArrayList;


public class MemoryUserDAO {
    private static ArrayList<UserData> users = new ArrayList<>();

    static public void clearUserDB() {
        users.clear();
    }
}
