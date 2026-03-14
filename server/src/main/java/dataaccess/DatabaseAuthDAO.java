package dataaccess;

import model.AuthData;
import model.RegisterResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.NULL;

public class DatabaseAuthDAO implements AuthDAO {

    public DatabaseAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clearAuthDB() throws DataAccessException {
        var statement = "TRUNCATE auths";
        DatabaseDAOHelpers.executeUpdate(statement);
    }

    @Override
    public RegisterResult addAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT into auths (authToken, username) VALUES (?, ?)";
        DatabaseDAOHelpers.executeUpdate(statement, authData.authToken(), authData.username());
        return new RegisterResult(authData.username(), authData.authToken());
    }

    @Override
    public boolean findAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auths WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return false;
    }


    @Override
    public void removeAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auths WHERE authToken=?";
        DatabaseDAOHelpers.executeUpdate(statement, authToken);
    }

    @Override
    public String findUsername(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM auths WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readString(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("unable to update database: %s", e.getMessage()));
        }
        return null; //this may not be what we're actually tryna return
    }

    private String readString(ResultSet rs) throws SQLException {
        return rs.getString("username");
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auths (
            `authToken` VARCHAR(255) NOT NULL,
            `username` VARCHAR(255) NOT NULL,
            PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseUserDAO.configureDatabase(createStatements);
    }
}
