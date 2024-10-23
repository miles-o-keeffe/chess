package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    void clear() throws DataAccessException;

    UserData createUser(UserData newUser) throws DataAccessException;

    UserData getUser(String userName) throws DataAccessException;

    int createGame(String gameName) throws DataAccessException; // Returns the gameID

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGame() throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    AuthData createAuth(String userName) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    AuthData getAuthByUsername(String userName) throws DataAccessException; // For testing

    void deleteAuth(AuthData authData) throws DataAccessException;
}
