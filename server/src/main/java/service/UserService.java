package service;

import dataaccess.MemoryDataAccess;
import model.*;

public class UserService {

    private final MemoryDataAccess memoryDataAccess;

    public UserService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }


    public RegisterResult register(RegisterRequest registerRequest) {

        //takes a user and sends it to MDA to #put the guy in
    }
    public LoginResult login(LoginRequest loginRequest) {}
    public void logout(LogoutRequest logoutRequest) {}
}