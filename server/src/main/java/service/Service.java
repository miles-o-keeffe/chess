package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

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
        AuthData authData = dataAccess.getAuth(logoutRequest.authToken());

        if (authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

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

    public int createGame(UserData user) {
        return 0;
    }

    public void joinGame(AuthData auth) {
    }
}
