package dataaccess;

import model.*;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO{
    private static ArrayList<AuthData> auths = new ArrayList<>();

    public void clearAuthDB() {
        auths.clear();
    }

    public RegisterResult addAuth(AuthData authData) {
        auths.add(authData);
        return new RegisterResult(authData.username(), authData.authToken());
    }
}
