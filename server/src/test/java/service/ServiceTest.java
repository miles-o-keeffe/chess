package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.*;
import result.*;

import java.util.ArrayList;
import java.util.Locale;

class ServiceTest {
    private static final DataAccess DATA_ACCESS = new MemoryDataAccess();
    private static final Service SERVICE = new Service(DATA_ACCESS);
    private final String testUsername = "test_username";
    private final String testPassword = "test_password";
    private final String testEmail = "test_email";
    private final String testGameName = "test_game";
    private final RegisterRequest testRegister = new RegisterRequest(testUsername, testPassword, testEmail);

    @BeforeEach
    void clear() throws ResponseException {
        SERVICE.clear();
    }

    @Test
    void clearTest() throws ResponseException, DataAccessException {
        for (int i = 0; i < 10; i++) {
            RegisterRequest testRegister = new RegisterRequest(testUsername + ((char) i), testPassword + ((char) i), testEmail + ((char) i));
            SERVICE.register(testRegister);
            DATA_ACCESS.createGame(testGameName + ((char) i));
        }

        SERVICE.clear();

        Assertions.assertTrue(DATA_ACCESS.isEmpty());
    }

    @Test
    void positiveRegister() throws ResponseException, DataAccessException {
        SERVICE.register(testRegister);

        UserData userData = DATA_ACCESS.getUser(testUsername);
        Assertions.assertNotNull(userData, "User not added");
        Assertions.assertEquals(testUsername, userData.username(), "Username not added to user data");
        Assertions.assertEquals(testPassword, userData.password(), "Password not added to user data");
        Assertions.assertEquals(testEmail, userData.email(), "Email not added to user data");

        AuthData authData = DATA_ACCESS.getAuthByUsername(testUsername);
        Assertions.assertNotNull(authData, "Auth data not created");
    }

    @Test
    void negativeRegister() throws ResponseException, DataAccessException {
        SERVICE.register(testRegister);
        ResponseException responseException = Assertions.assertThrows(ResponseException.class, () -> {
            SERVICE.register(testRegister);
        });
        Assertions.assertEquals(responseException.statusCode(), 403);

        RegisterRequest testRegNull = new RegisterRequest(null, null, null);
        responseException = Assertions.assertThrows(ResponseException.class, () -> {
            SERVICE.register(testRegNull);
        });
        Assertions.assertEquals(responseException.statusCode(), 400);

        RegisterRequest testRegPartialNull = new RegisterRequest("hello", null, "hello");
        responseException = Assertions.assertThrows(ResponseException.class, () -> {
            SERVICE.register(testRegPartialNull);
        });
        Assertions.assertEquals(responseException.statusCode(), 400);
    }

    @Test
    void positiveLogin() throws ResponseException, DataAccessException {
        SERVICE.register(testRegister);
        LoginResult loginResult = SERVICE.login(new LoginRequest(testUsername, testPassword));

        Assertions.assertNotNull(loginResult, "Return was null");
        Assertions.assertEquals(loginResult.username(), testUsername, "Usernames do not Match");
        Assertions.assertEquals(DATA_ACCESS.getAuth(loginResult.authToken()).username(), testUsername);
    }

    @Test
    void negativeLogin() throws ResponseException, DataAccessException {
        SERVICE.register(testRegister);
        ResponseException responseException = Assertions.assertThrows(ResponseException.class, () -> {
            SERVICE.login(new LoginRequest(testUsername, "wrongpassword"));
        });
        Assertions.assertEquals(responseException.statusCode(), 401);

        responseException = Assertions.assertThrows(ResponseException.class, () -> {
            SERVICE.login(new LoginRequest("wrongusername", "wrongpassword"));
        });
        Assertions.assertEquals(responseException.statusCode(), 401);
    }

    @Test
    void positiveLogout() throws ResponseException, DataAccessException {
        SERVICE.register(testRegister);
        LoginResult loginResult = SERVICE.login(new LoginRequest(testUsername, testPassword));
        LogoutResult logoutResult = SERVICE.logout(new LogoutRequest(loginResult.authToken()));

        Assertions.assertEquals(new LogoutResult(), logoutResult);
        Assertions.assertNull(DATA_ACCESS.getAuth(testUsername));
    }

    @Test
    void negativeLogout() throws ResponseException, DataAccessException {
        ResponseException responseException = Assertions.assertThrows(ResponseException.class, () -> {
            SERVICE.logout(new LogoutRequest("bad-auth-token"));
        });
        Assertions.assertEquals(responseException.statusCode(), 401);
    }

    @Test
    void positiveCreateGame() throws ResponseException, DataAccessException {
        RegisterResult registerResult = SERVICE.register(testRegister);
        CreateGameResult createGameResult = SERVICE.createGame(new CreateGameRequest("testGameName"), registerResult.authToken());

        Assertions.assertEquals(DATA_ACCESS.getGame(createGameResult.gameID()), new GameData(createGameResult.gameID(),
                null, null, "testGameName", new ChessGame()));
        Assertions.assertEquals(DATA_ACCESS.getGame(createGameResult.gameID()).gameName(), "testGameName");
        Assertions.assertNotEquals(DATA_ACCESS.getGame(createGameResult.gameID()).gameName(), "wrongName");
    }

    @Test
    void negativeCreateGame() throws ResponseException, DataAccessException {
        ResponseException responseException = Assertions.assertThrows(ResponseException.class, () -> {
            SERVICE.createGame(new CreateGameRequest("testGameName"), "bad-auth-token");
        });
        Assertions.assertEquals(responseException.statusCode(), 401);
        Assertions.assertEquals(responseException.getMessage(), "Error: unauthorized");
    }

    @Test
    void positiveListGame() throws ResponseException, DataAccessException {
        
    }

    @Test
    void negativeGame() throws ResponseException, DataAccessException {
    }

    @Test
    void positiveJoinGame() throws ResponseException, DataAccessException {
    }

    @Test
    void negativeJoinGame() throws ResponseException, DataAccessException {
    }
}
