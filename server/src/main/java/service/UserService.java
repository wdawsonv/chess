package service;

import dataaccess.*;
import model.*;
import org.eclipse.jetty.server.Authentication;


public class UserService {
    private static final UserDAO userDAO = new MemoryUserDAO();

    public static void clear() {
        userDAO.clearUserDB();
    }

    public static void register(RegisterRequest registerRequest) throws AlreadyTakenException, BadRequestException {
        if (getUser(registerRequest.username())) {
            throw new AlreadyTakenException("already taken");
        } else if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException("bad request in register");
        } else {
                UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
                userDAO.createUser(userData);
        }
        //is name taken? if not then create user and auth and return registerresult
    }

    //helper
    public static boolean getUser(String username) {
        return userDAO.getUser(username);
    }

    public static boolean checkLogin(LoginRequest loginRequest) throws UnauthorizedException, BadRequestException {
        String username = loginRequest.username(), password = loginRequest.password();

        if (username == null || password == null) {
            throw new BadRequestException("bad request");
        } else if (!getUser(username)) {
            throw new UnauthorizedException("unauthorized");
        } else {
            UserData userData = userDAO.getUserData(username);
            if (!password.equals(userData.password())) {
                throw new UnauthorizedException("unauthorized");
            } else {
                return true;
            }
        }
    }

}
