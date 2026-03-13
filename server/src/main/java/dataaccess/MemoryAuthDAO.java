package dataaccess;

import chess.ChessPiece;
import model.*;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO{
    private static ArrayList<AuthData> auths = new ArrayList<>();

    @Override
    public void clearAuthDB() {
        auths.clear();
    }

    @Override
    public RegisterResult addAuth(AuthData authData) {
        auths.add(authData);
        return new RegisterResult(authData.username(), authData.authToken());
    }

    @Override
    public boolean findAuth(String authToken) {
        for (AuthData auth : auths) {
            if (auth.authToken().equals(authToken)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeAuth(String authToken) {
        auths.removeIf(auth -> auth.authToken().equals(authToken));
    }
}
