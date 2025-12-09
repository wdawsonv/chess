package dataaccess;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import model.*;
import dataaccess.DataAccessException;
import org.mindrot.jbcrypt.BCrypt;
import service.AlreadyTakenException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess {

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
    private int gameID = 1000;
    private static final Gson gson = createSerializer();

    public void MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    //add game serializer for ChessGame types
    public static Gson createSerializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(ChessPiece.class,
                (JsonDeserializer<ChessPiece>) (el, type, ctx) -> {
                    ChessPiece chessPiece = null;
                    if (el.isJsonObject()) {
                        String pieceType = el.getAsJsonObject().get("type").getAsString();
                        chessPiece = ctx.deserialize(el, ChessPiece.class);
//                        switch (ChessPiece.PieceType.valueOf(pieceType)) {
//                            case PAWN -> chessPiece = ctx.deserialize(el, Pawn.class);
//                            case ROOK -> chessPiece = ctx.deserialize(el, Rook.class);
//                            case KNIGHT -> chessPiece = ctx.deserialize(el, Knight.class);
//                            case BISHOP -> chessPiece = ctx.deserialize(el, Bishop.class);
//                            case QUEEN -> chessPiece = ctx.deserialize(el, Queen.class);
//                            case KING -> chessPiece = ctx.deserialize(el, King.class);
//                        }
                    }
                    return chessPiece;
                });

        return gsonBuilder.create();
    }

    //add a user
    //will take in UserData type and return userData
    public UserData addUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        executeUpdate(statement, user.username(), hashedPassword, user.email());

        return user;
    }

    //add auth
    public String addAuth(String username) throws DataAccessException {
        String authToken = generateToken();
        var statement = "INSERT INTO auths (username, authToken) VALUES (?, ?)";

        executeUpdate(statement, username, authToken);

        return authToken;
    }

    public CreateResult createNewGame(String gamename) throws DataAccessException {
        gameID++;
        String json = gson.toJson(new ChessGame());
        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gamename, json) VALUES (?, ?, ?, ?, ?)";

        executeUpdate(statement, gameID, null, null, gamename, json);

        return new CreateResult(gameID);
    }

    public void removeAuth(AuthData auth) throws DataAccessException {
        var statement = "DELETE FROM auths WHERE auth=?";
        executeUpdate(statement, auth);
    }

    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM users where username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    public JoinResult joinExistingGame(int gameID, String color, String username) throws DataAccessException {
        //check to see if the name is free
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername FROM games where gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID); //what is this line doing?
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        boolean isEmptyWhite = isNullWhite(rs);
                        boolean isEmptyBlack = isNullBlack(rs);

                        if (color.equals("WHITE") && isEmptyWhite || color.equals("BLACK") && isEmptyBlack) {
                            return addUserToColor(gameID, color, username);
                        } else {
                            throw new AlreadyTakenException("someone already there/bad color");
                        }
                    }
                }
            }
        } catch (Exception e) { //maybe separate this out so it's not
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    private JoinResult addUserToColor(int gameID, String color, String username) throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            if (color.equals("WHITE")) {
                String statement = "UPDATE games SET whiteUsername=? WHERE gameID=?";
                executeUpdate(statement, username, gameID);
            } else {
                String statement = "UPDATE games SET blackUsername=? WHERE gameID=?";
                executeUpdate(statement, username, gameID);
            }
            return new JoinResult();
        }
    }

    private boolean isNullWhite(ResultSet rs) throws SQLException {
        var whiteUsername = rs.getString("whiteUsername");
        return (whiteUsername == null);
    }

    private boolean isNullBlack(ResultSet rs) throws SQLException {
        var blackUsername = rs.getString("blackUsername");
        return (blackUsername == null);
    }

    public List<GameData> getGamesList() throws DataAccessException, SQLException {
        var result = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gamename, json FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("error: " + e.getMessage());
        }
        return result;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gamename = rs.getString("gamename");
        var json = rs.getString("json");
        ChessGame game = gson.fromJson(json, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gamename, game);
    }

    public AuthData getAuth(String token) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auths where authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, token);
                try (ResultSet rs = ps.executeQuery())  {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    //what is a resultset?
    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    public void clearUserData() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    public void clearAuthData() throws DataAccessException {
        var statement = "TRUNCATE auths";
        executeUpdate(statement);
    }

    public void clearGameData() throws DataAccessException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    public List<UserData> listUsers() throws DataAccessException, SQLException {
        var result = new ArrayList<UserData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM uesrs";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readUser(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("error: " + e.getMessage());
        }
        return result;
    }






    //add a game

    //list all users
    //list auths?

    //given a username return the userdata
    //given an auth return authdata

    //remove an auth from auths
    //list all games

    //take a user and put as a color on a game



    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
//                    else if (param instanceof PetType p) ps.setString(i + 1, p.toString()):
                else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


//    boolean verifyUser(String username, String providedClearTextPassword) {
//        var hashedPassowrd = readHashedPasswordFromDataabase(username);
//
//        return BCrypt.checkpw(providedClearTextPassword, hashedPassowrd);
//    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                `username` varchar(256) NOT NULL,
                `password` varchar(256) NOT NULL,
                `email` varchar(256) NOT NULL,
                PRIMARY KEY (`username`),
                INDEX(username)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS auths (
                `authToken` varchar(256) NOT NULL,
                `username` varchar(256) NOT NULL,
                PRIMARY KEY (`authToken`),
                INDEX(username)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
                `gameID` int NOT NULL AUTO_INCREMENT,
                `whiteUsername` varchar(256),
                `blackUsername` varchar(256),
                `gameName` varchar(256) NOT NULL,
                `json` TEXT NOT NULL,
                PRIMARY KEY (`gameID`),
                INDEX(`gameName`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """ //implement the acutal game as just json the way petshop does :PPPPPPPP
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("if you want more info go implement /hared/src/main/exception/ResponseException.java: " + ex.getMessage());
        }
    }
}
