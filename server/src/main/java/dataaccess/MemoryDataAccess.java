package dataaccess;

import chess.ChessGame;
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
    public int createGame(String gameName) throws DataAccessException {
        Set<Integer> existingIds = new HashSet<>();
        int newGameID = 1;
        for (GameData gameData : games) {
            existingIds.add(gameData.gameID());
        }

        while (existingIds.contains(newGameID)) {
            newGameID += 1;
        }
        games.add(new GameData(newGameID, null, null, gameName, new ChessGame()));

        return newGameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData gameData : games) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        return null;
    }

    @Override
    public Collection<GameData> listGame() throws DataAccessException {
        return new ArrayList<>(games);
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).gameID() == gameData.gameID()) {
                games.set(i, gameData);
            }
        }
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

    // For testing
    public AuthData getAuthByUsername(String userName) throws DataAccessException {
        for (AuthData authData : authentications) {
            if (Objects.equals(authData.username(), userName)) {
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
