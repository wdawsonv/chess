package service;

import dataaccess.UserDAO;

public class UserService {
    public static void clear() {
        UserDAO.clearUserDB();
    }
}
