package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    void clear();

    UserData createUser(UserData newUser);

    UserData getUser(String userName);

    int createGame(); // Returns the gameID

    GameData getGame();

    Collection<GameData> listGame();

    void updateGame();

    AuthData createAuth(String userName);

    AuthData getAuth();

    void deleteAuth();
}
