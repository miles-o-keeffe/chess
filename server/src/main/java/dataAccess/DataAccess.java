package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    void clear();

    UserData createUser(UserData newUser) throws DataAccessException;

    UserData getUser(String userName) throws DataAccessException;

    int createGame() throws DataAccessException; // Returns the gameID

    GameData getGame() throws DataAccessException;

    Collection<GameData> listGame() throws DataAccessException;

    void updateGame() throws DataAccessException;

    AuthData createAuth(String userName) throws DataAccessException;

    AuthData getAuth() throws DataAccessException;

    void deleteAuth() throws DataAccessException;
}
