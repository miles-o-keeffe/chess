package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
    public boolean isEmpty() throws DataAccessException {
        String[] tables = {"users", "games", "authentications"};
        for (String table : tables) {
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT * FROM " + table;
                if (executeIsEmptyStatement(conn, statement)) {
                    return false;
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }

        return true;
    }

    private static boolean executeIsEmptyStatement(Connection conn, String statement) throws SQLException {
        try (var preparedStatement = conn.prepareStatement(statement)) {
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
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
                        var hashedPass = rs.getString("password");
                        var email = rs.getString("email");

                        return new UserData(username, hashedPass, email);
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
        var statement = "SELECT * FROM games WHERE id=?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);

                try (var rs = preparedStatement.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    } else {
                        String whiteUsername = rs.getString("white_username");
                        String blackUsername = rs.getString("black_username");
                        String gameName = rs.getString("game_name");
                        ChessGame chessGame = new Gson().fromJson(rs.getString("chess_game"), ChessGame.class);

                        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public ArrayList<GameData> listGame() throws DataAccessException {
        ArrayList<GameData> listOfGames = new ArrayList<>();

        var statement = "SELECT * FROM games;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {

                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        int gameID = rs.getInt("id");
                        String whiteUsername = rs.getString("white_username");
                        String blackUsername = rs.getString("black_username");
                        String gameName = rs.getString("game_name");
                        ChessGame chessGame = new Gson().fromJson(rs.getString("chess_game"), ChessGame.class);

                        listOfGames.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
                    }
                }
                return listOfGames;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        var statement = "UPDATE games SET white_username=?,black_username=?,game_name=?,chess_game=? WHERE id = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, gameData.whiteUsername());
                preparedStatement.setString(2, gameData.blackUsername());
                preparedStatement.setString(3, gameData.gameName());
                preparedStatement.setString(4, new Gson().toJson(gameData.game()));
                preparedStatement.setInt(5, gameData.gameID());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
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

                return executeGetAuthData(preparedStatement);
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

                return executeGetAuthData(preparedStatement);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private static AuthData executeGetAuthData(PreparedStatement preparedStatement) throws SQLException {
        try (var rs = preparedStatement.executeQuery()) {
            if (!rs.next()) {
                return null;
            } else {
                var authToken = rs.getString("auth_token");
                var username = rs.getString("username");

                return new AuthData(authToken, username);
            }
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
