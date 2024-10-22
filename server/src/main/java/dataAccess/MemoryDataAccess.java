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
    public void clear() throws DataAccessException {
        users.clear();
        games.clear();
        authentications.clear();
    }

    @Override
    public UserData createUser(UserData newUser) throws DataAccessException {
        for (UserData userData : users) {
            if (Objects.equals(userData.username(), newUser.username())) {
                return null;
            }
        }
        users.add(newUser);
        return newUser;
    }

    @Override
    public UserData getUser(String userName) throws DataAccessException {
        for (UserData userData : users) {
            if (Objects.equals(userData.username(), userName)) {
                return userData;
            }
        }
        return null;
    }

    @Override
    public int createGame() throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame() throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGame() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame() throws DataAccessException {

    }

    @Override
    public AuthData createAuth(String userName) throws DataAccessException {
        AuthData newAuthData = new AuthData(UUID.randomUUID().toString(), userName);
        authentications.add(newAuthData);
        return newAuthData;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData authData : authentications) {
            if (Objects.equals(authData.authToken(), authToken)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        authentications.remove(authData);
    }
}
