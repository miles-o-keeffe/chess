package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException, ResponseException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        String[] tables = {"users", "games", "authentications"};
        try (var conn = DatabaseManager.getConnection()) {
            for (String table : tables) {
                var statement = "TRUNCATE TABLE " + table + ";";
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


    @Override
    public UserData createUser(UserData newUser) throws DataAccessException {

        // Checks if username is already taken
        if (getUser(newUser.username()) != null) {
            return null;
        }

        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                String hashedPassword = hashPassword(newUser.password());
                preparedStatement.setString(1, newUser.username());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, newUser.email());

                preparedStatement.executeUpdate();

                return newUser;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String userName) throws DataAccessException {
        var statement = "SELECT * FROM users WHERE username=?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, userName);

                try (var rs = preparedStatement.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    } else {
                        var username = rs.getString("username");
                        var hashed_pass = rs.getString("password");
                        var email = rs.getString("email");

                        return new UserData(username, hashed_pass, email);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO games (game_name, chess_game) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ChessGame chessGame = new ChessGame();

                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, new Gson().toJson(chessGame));

                preparedStatement.executeUpdate();

                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return -1;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public ArrayList<GameData> listGame() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public AuthData createAuth(String userName) throws DataAccessException {
        AuthData newAuthData = new AuthData(UUID.randomUUID().toString(), userName);
        var statement = "INSERT INTO authentications (auth_token, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, newAuthData.authToken());
                preparedStatement.setString(2, newAuthData.username());

                preparedStatement.executeUpdate();

                return newAuthData;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT * FROM authentications WHERE auth_token=?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);

                try (var rs = preparedStatement.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    } else {
                        var auth_token = rs.getString("auth_token");
                        var username = rs.getString("username");

                        return new AuthData(auth_token, username);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuthByUsername(String userName) throws DataAccessException {
        var statement = "SELECT * FROM authentications WHERE username=?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, userName);

                try (var rs = preparedStatement.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    } else {
                        var auth_token = rs.getString("auth_token");
                        var username = rs.getString("username");

                        return new AuthData(auth_token, username);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        var statement = "DELETE FROM authentications WHERE auth_token=?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL UNIQUE,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            ) CHARSET=utf8mb4
            """,
            """
            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL AUTO_INCREMENT,
              `white_username` varchar(256) DEFAULT NULL,
              `black_username` varchar(256) DEFAULT NULL,
              `game_name` varchar(256) NOT NULL,
              `chess_game` TEXT NOT NULL,
              PRIMARY KEY (`id`)
            ) CHARSET=utf8mb4
            """,
            """
            CREATE TABLE IF NOT EXISTS  authentications (
              `auth_token` varchar(256) NOT NULL UNIQUE,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`auth_token`)
            ) CHARSET=utf8mb4
            """
    };

    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var createStatement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(createStatement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
