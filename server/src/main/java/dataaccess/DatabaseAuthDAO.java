package dataaccess;

import model.AuthData;
import model.RegisterResult;

public class DatabaseAuthDAO implements AuthDAO {
    @Override
    public void clearAuthDB() {

    }

    @Override
    public RegisterResult addAuth(AuthData authData) {
        return null;
    }

    @Override
    public boolean findAuth(String authToken) {
        return false;
    }

    @Override
    public void removeAuth(String authToken) {

    }

    @Override
    public String findUsername(String authToken) {
        return "";
    }
}
