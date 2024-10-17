package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccess implements DataAccess {
    // final private map<String, UserData> users = new HashMap<>();

    @Override
    public void clear() {

    }

    @Override
    public UserData createUser(UserData newUser) {
        return null;
    }

    @Override
    public UserData getUser(String userName) {
        return null;
    }

    @Override
    public int createGame() {
        return 0;
    }

    @Override
    public GameData getGame() {
        return null;
    }

    @Override
    public Collection<GameData> listGame() {
        return List.of();
    }

    @Override
    public void updateGame() {

    }

    @Override
    public AuthData createAuth() {
        return null;
    }

    @Override
    public AuthData getAuth() {
        return null;
    }

    @Override
    public void deleteAuth() {

    }
}
