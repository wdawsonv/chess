package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import model.*;
import java.util.UUID;

public class AuthService {
    private static final AuthDAO authDAO = new MemoryAuthDAO();

    public static void clear() {
        authDAO.clearAuthDB();
    }

    public static RegisterResult createAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        return authDAO.addAuth(authData);
    }
}
