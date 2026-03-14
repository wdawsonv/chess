package service;

import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;


public class UserService {
    private static final UserDAO USER_DAO;

    static {
        try {
            USER_DAO = new DatabaseUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clear() throws DataAccessException {
        USER_DAO.clearUserDB();
    }

    public static void register(RegisterRequest registerRequest) throws AlreadyTakenException, BadRequestException, DataAccessException {
        if (getUser(registerRequest.username())) {
            throw new AlreadyTakenException("already taken");
        } else if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException("bad request in register");
        } else {
                String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
                UserData userData = new UserData(registerRequest.username(), hashedPassword, registerRequest.email());
                USER_DAO.createUser(userData);
        }
        //is name taken? if not then create user and auth and return registerresult
    }

    //helper
    public static boolean getUser(String username) throws DataAccessException {
        return USER_DAO.getUser(username);
    }

    public static boolean checkLogin(LoginRequest loginRequest) throws UnauthorizedException, BadRequestException, DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();

        if (username == null || password == null) {
            throw new BadRequestException("bad request");
        } else if (!getUser(username)) {
            throw new UnauthorizedException("unauthorized");
        } else {
            UserData userData = USER_DAO.getUserData(username);
            if (!BCrypt.checkpw(password, userData.password())) {
                throw new UnauthorizedException("unauthorized");
            } else {
                return true;
            }
        }
    }

}
