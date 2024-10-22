package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

public class Service {
    private final DataAccess dataAccess = new MemoryDataAccess();

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        try {
            dataAccess.createUser(newUser);
        } catch (DataAccessException e) {
            throw new ResponseException(403, e.getMessage());
        }
        AuthData newAuthData = dataAccess.createAuth(newUser.username());
        return new RegisterResult(newAuthData.username(), newAuthData.authToken());
    }

    public AuthData login(UserData user) {
        return null;
    }

    public void logout(AuthData auth) {
    }

    public void clear() {
    }

    public int createGame(UserData user) {
        return 0;
    }

    public void joinGame(AuthData auth) {
    }
}
