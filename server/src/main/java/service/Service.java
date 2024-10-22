package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.*;
import result.*;

import java.util.ArrayList;
import java.util.Objects;

public class Service {
    private final DataAccess dataAccess = new MemoryDataAccess();

    public RegisterResult register(RegisterRequest request) throws ResponseException, DataAccessException {
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        var userCreated = dataAccess.createUser(newUser);
        if (userCreated == null) {
            throw new ResponseException(403, "Error: already taken");
        }
//        try {
//            dataAccess.createUser(newUser);
//        } catch (DataAccessException e) {
//            int errorCode = 500;
//            if (Objects.equals(e.getMessage(), "Error: already taken")) {
//                errorCode = 403;
//            }
//            throw new ResponseException(errorCode, e.getMessage());
//        }
        AuthData newAuthData = dataAccess.createAuth(newUser.username());
        return new RegisterResult(newAuthData.username(), newAuthData.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException, DataAccessException {
        var userData = dataAccess.getUser(loginRequest.username());
        if (userData == null) {
            throw new ResponseException(500, "Error: no such username exists");
        }

        if (!Objects.equals(userData.password(), loginRequest.password())) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        AuthData newAuthData = dataAccess.createAuth(loginRequest.username());
        return new LoginResult(newAuthData.username(), newAuthData.authToken());
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException, ResponseException {
        AuthData authData = authenticate(logoutRequest.authToken());

        dataAccess.deleteAuth(authData);
        return new LogoutResult();
    }

    public void clear() throws ResponseException {
        try {
            dataAccess.clear();
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws ResponseException, DataAccessException {
        authenticate(listGamesRequest.authToken());

        var listOfGames = dataAccess.listGame();
        ListGamesResult listGamesResult = new ListGamesResult(new ArrayList<>());
        if (listOfGames == null) {
            return listGamesResult;
        }
        for (GameData gameData : listOfGames) {
            listGamesResult.listOfGames().add(new ListGameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName()));
        }
        return listGamesResult;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException, ResponseException {
        authenticate(createGameRequest.authToken());

        int gameID = dataAccess.createGame(createGameRequest.gameName());

        return new CreateGameResult(gameID);
    }

    public void joinGame(AuthData auth) {
    }

    private AuthData authenticate(String authToken) throws DataAccessException, ResponseException {
        AuthData authData = dataAccess.getAuth(authToken);

        if (authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        return authData;
    }
}
