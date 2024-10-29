package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public class MySqlDataAccess implements DataAccess {

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  use (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(type),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public MySqlDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public UserData createUser(UserData newUser) throws DataAccessException {
        return null;
    }

    @Override
    public UserData getUser(String userName) throws DataAccessException {
        return null;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
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
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuthByUsername(String userName) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {

    }
}
