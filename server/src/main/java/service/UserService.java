package service;

import dataaccess.*;
import model.*;
import org.eclipse.jetty.server.Authentication;


public class UserService {
    private static final UserDAO userDAO = new MemoryUserDAO();

    public static void clear() {
        userDAO.clearUserDB();
    }

    public static boolean register(RegisterRequest registerRequest) {
        if (getUser(registerRequest.username())) {
            //username is already in there, return false
            return false;
        } else {
            UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            userDAO.createUser(userData);
            return true;
        }
        //is name taken? if not then create user and auth and return registerresult
    }

    //helper
    public static boolean getUser(String username) {
        return userDAO.getUser(username);
    }

}
