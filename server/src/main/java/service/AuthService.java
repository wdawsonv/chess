package service;

import dataaccess.AuthDAO;
import dataaccess.DatabaseAuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.UnauthorizedException;
import model.*;
import java.util.UUID;

public class AuthService {
    private static final AuthDAO AUTH_DAO = new DatabaseAuthDAO();

    public static void clear() {
        AUTH_DAO.clearAuthDB();
    }

    public static RegisterResult createAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        return AUTH_DAO.addAuth(authData);
    }

    public static void logout(LogoutRequest logoutRequest) throws UnauthorizedException {
        String authToken = logoutRequest.authToken();

        if (!findAuth(authToken)) {
            throw new UnauthorizedException("unauthorized");
        } else {
            AUTH_DAO.removeAuth(authToken);
        }
    }

    public static boolean findAuth(String authToken) throws UnauthorizedException {
        if (!AUTH_DAO.findAuth(authToken)) {
            throw new UnauthorizedException("unauthorized");
        } else {
            return true;
        }
    }

    public static String findAuthReturnUsername(String authToken) throws UnauthorizedException {
        if (!AUTH_DAO.findAuth(authToken)) {
            throw new UnauthorizedException("unauthorized");
        } else {
            return AUTH_DAO.findUsername(authToken);
        }
    }
}
