package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.*;
import result.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class Service {
    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException, DataAccessException {
        UserData newUser = new UserData(request.username(), request.password(), request.email());

        if (newUser.email() == null || newUser.username() == null || newUser.password() == null) {
            throw new ResponseException(400, "Error: bad request");
        } else if (newUser.email().isBlank() || newUser.username().isBlank() || newUser.password().isBlank()) {
            throw new ResponseException(400, "Error: bad request");
        }

        var userCreated = dataAccess.createUser(newUser);
        if (userCreated == null) {
            throw new ResponseException(403, "Error: already taken");
        }
        AuthData newAuthData = dataAccess.createAuth(newUser.username());
        return new RegisterResult(newAuthData.username(), newAuthData.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException, DataAccessException {
        var userData = dataAccess.getUser(loginRequest.username());
        if (userData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        // Compares hashed password if we are using MySQL
        if (dataAccess instanceof MySqlDataAccess) {
            if (!BCrypt.checkpw(loginRequest.password(), userData.password())) {
                throw new ResponseException(401, "Error: unauthorized");
            }
        } else {
            if (!Objects.equals(userData.password(), loginRequest.password())) {
                throw new ResponseException(401, "Error: unauthorized");
            }
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
            listGamesResult.games().add(new ListGameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName()));
        }
        return listGamesResult;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws DataAccessException, ResponseException {
        if (createGameRequest.gameName() == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        authenticate(authToken);

        int gameID = dataAccess.createGame(createGameRequest.gameName());

        return new CreateGameResult(gameID);
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest, String authToken) throws ResponseException, DataAccessException {
        authenticate(authToken);
        GameData gameData = dataAccess.getGame(joinGameRequest.gameID());

        if (gameData == null) {
            throw new ResponseException(500, "Error: no such game exists");
        }

        if ((Objects.equals(joinGameRequest.playerColor(), "WHITE") && gameData.whiteUsername() != null)
                || (Objects.equals(joinGameRequest.playerColor(), "BLACK") && gameData.blackUsername() != null)) {
            throw new ResponseException(403, "Error: already taken");
        }

        String username = dataAccess.getAuth(authToken).username();

        GameData newGameData = getGameData(joinGameRequest, username, gameData);

        dataAccess.updateGame(newGameData);

        return new JoinGameResult();
    }

    private static GameData getGameData(JoinGameRequest joinGameRequest, String username, GameData gameData) throws ResponseException {
        GameData newGameData;
        if (Objects.equals(joinGameRequest.playerColor(), "WHITE")) {
            newGameData = new GameData(joinGameRequest.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else if (Objects.equals(joinGameRequest.playerColor(), "BLACK")) {
            newGameData = new GameData(joinGameRequest.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
        } else {
            throw new ResponseException(400, "Error: bad request");
        }
        return newGameData;
    }

    private AuthData authenticate(String authToken) throws DataAccessException, ResponseException {
        AuthData authData = dataAccess.getAuth(authToken);

        if (authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        return authData;
    }
}
