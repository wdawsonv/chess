package service;

import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.*;
import org.eclipse.jetty.server.Authentication;

public class UserService {
    private static final UserDAO userDAO = new MemoryUserDAO();

    public static void clear() {
        userDAO.clearUserDB();
    }

    public static void register(RegisterRequest registerRequest) {
        if (getUser(registerRequest.username())) {
            //throw da error
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

}
