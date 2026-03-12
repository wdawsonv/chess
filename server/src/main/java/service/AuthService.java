package service;

import dataaccess.AuthDAO;

public class AuthService {
    public static void clear() {
        AuthDAO.clearAuthDB();
    }
}
