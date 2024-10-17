package service;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

public class UserService {
    private final DataAccess dataAccess = new MemoryDataAccess();

    public AuthData register(UserData user) {
        dataAccess.createUser(user);
        dataAccess.createAuth(user.username());
        AuthData newAuthData = dataAccess.createAuth(user.username());
        return newAuthData;
    }

    public AuthData login(UserData user) {
        return null;
    }

    public void logout(AuthData auth) {
    }
}
