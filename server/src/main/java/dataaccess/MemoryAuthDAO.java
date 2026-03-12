package dataaccess;

import model.*;

import java.util.ArrayList;

public class MemoryAuthDAO {
    private static ArrayList<AuthData> auths = new ArrayList<>();

    static public void clearAuthDB() {
        auths.clear();
    }
}
