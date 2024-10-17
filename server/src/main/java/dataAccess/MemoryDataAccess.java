package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    final private ArrayList<UserData> users = new ArrayList<>();
    final private ArrayList<GameData> games = new ArrayList<>();
    final private ArrayList<AuthData> authentications = new ArrayList<>();

    @Override
    public void clear() {
        users.clear();
        games.clear();
        authentications.clear();
    }

    @Override
    public UserData createUser(UserData newUser) {
        users.add(newUser);
        return newUser;
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
    public AuthData createAuth(String userName) {
        AuthData newAuthData = new AuthData("testAuthToken", userName);
        authentications.add(newAuthData);
        return newAuthData;
    }

    @Override
    public AuthData getAuth() {
        return null;
    }

    @Override
    public void deleteAuth() {

    }
}
