package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.UnauthorizedException;
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

    public static void logout(LogoutRequest logoutRequest) throws UnauthorizedException {
        String authToken = logoutRequest.authToken();

        if (!authDAO.findAuth(authToken)) {
            throw new UnauthorizedException("unauthorized");
        } else {
            authDAO.removeAuth(authToken);
        }
    }
}
