package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import request.*;
import result.*;

import java.util.ArrayList;

class ServiceTest {
    // private static final DataAccess DATA_ACCESS = new MemoryDataAccess();
    private static DataAccess dataAccess;
    private static Service service;
    private final String testUsername = "test_username";
    private final String testPassword = "test_password";
    private final String testEmail = "test_email";
    private final RegisterRequest testRegister = new RegisterRequest(testUsername, testPassword, testEmail);

    @BeforeAll
    static void createDatabase() throws ResponseException, DataAccessException {
        dataAccess = new MySqlDataAccess();
        service = new Service(dataAccess);
    }

    @BeforeEach
    void clear() throws ResponseException {
        service.clear();
    }

    @AfterAll
    static void finalClear() throws ResponseException {
        service.clear();
    }

    @Test
    void clearTest() throws ResponseException, DataAccessException {

        // Adds to all the DATA_ACCESS fields so that they can be cleared
        for (int i = 1; i < 11; i++) {
            RegisterRequest testRegister = new RegisterRequest(testUsername + (i),
                    testPassword + (i), testEmail + (i));
            service.register(testRegister);
            String testGameName = "test_game";
            dataAccess.createGame(testGameName + ((char) i));
        }

        service.clear();

        Assertions.assertTrue(dataAccess.isEmpty());
    }

    @Test
    void positiveRegister() throws ResponseException, DataAccessException {
        service.register(testRegister);

        UserData userData = dataAccess.getUser(testUsername);
        Assertions.assertNotNull(userData, "User not added");
        Assertions.assertEquals(testUsername, userData.username(), "Username not added to user data");
        // Assertions.assertEquals(testPassword, userData.password(), "Password not added to user data");
        Assertions.assertTrue(BCrypt.checkpw(testPassword, userData.password()));
        Assertions.assertEquals(testEmail, userData.email(), "Email not added to user data");

        AuthData authData = dataAccess.getAuthByUsername(testUsername);
        Assertions.assertNotNull(authData, "Auth data not created");
    }

    @Test
    void negativeRegister() throws ResponseException, DataAccessException {
        // Duplicate Username
        service.register(testRegister);
        ResponseException responseException = Assertions.assertThrows(ResponseException.class, () -> {
            service.register(testRegister);
        });
        Assertions.assertEquals(responseException.statusCode(), 403);

        // Null requests
        RegisterRequest testRegNull = new RegisterRequest(null, null, null);
        responseException = Assertions.assertThrows(ResponseException.class, () -> {
            service.register(testRegNull);
        });
        Assertions.assertEquals(responseException.statusCode(), 400);

        RegisterRequest testRegPartialNull = new RegisterRequest("hello", null, "hello");
        responseException = Assertions.assertThrows(ResponseException.class, () -> {
            service.register(testRegPartialNull);
        });
        Assertions.assertEquals(responseException.statusCode(), 400);
    }

    @Test
    void positiveLogin() throws ResponseException, DataAccessException {
        service.register(testRegister);
        LoginResult loginResult = service.login(new LoginRequest(testUsername, testPassword));

        Assertions.assertNotNull(loginResult, "Return was null");
        Assertions.assertEquals(loginResult.username(), testUsername, "Usernames do not Match");
        Assertions.assertEquals(dataAccess.getAuth(loginResult.authToken()).username(), testUsername);
    }

    @Test
    void negativeLogin() throws ResponseException, DataAccessException {
        service.register(testRegister);
        ResponseException responseException = Assertions.assertThrows(ResponseException.class, () -> {
            service.login(new LoginRequest(testUsername, "wrongpassword"));
        });
        Assertions.assertEquals(responseException.statusCode(), 401);

        responseException = Assertions.assertThrows(ResponseException.class, () -> {
            service.login(new LoginRequest("wrongusername", "wrongpassword"));
        });
        Assertions.assertEquals(responseException.statusCode(), 401);
    }

    @Test
    void positiveLogout() throws ResponseException, DataAccessException {
        service.register(testRegister);
        LoginResult loginResult = service.login(new LoginRequest(testUsername, testPassword));
        LogoutResult logoutResult = service.logout(new LogoutRequest(loginResult.authToken()));

        Assertions.assertEquals(new LogoutResult(), logoutResult);
        Assertions.assertNull(dataAccess.getAuth(testUsername));
    }

    @Test
    void negativeLogout() throws ResponseException, DataAccessException {
        ResponseException responseException = Assertions.assertThrows(ResponseException.class, () -> {
            service.logout(new LogoutRequest("bad-auth-token"));
        });
        Assertions.assertEquals(responseException.statusCode(), 401);
    }

    @Test
    void positiveCreateGame() throws ResponseException, DataAccessException {
        RegisterResult registerResult = service.register(testRegister);
        CreateGameResult createGameResult = service.createGame(new CreateGameRequest("testGameName"), registerResult.authToken());

        Assertions.assertEquals(dataAccess.getGame(createGameResult.gameID()), new GameData(createGameResult.gameID(),
                null, null, "testGameName", new ChessGame()));
        Assertions.assertEquals(dataAccess.getGame(createGameResult.gameID()).gameName(), "testGameName");
        Assertions.assertNotEquals(dataAccess.getGame(createGameResult.gameID()).gameName(), "wrongName");
    }

    @Test
    void negativeCreateGame() throws ResponseException, DataAccessException {
        ResponseException responseException = Assertions.assertThrows(ResponseException.class, () -> {
            service.createGame(new CreateGameRequest("testGameName"), "bad-auth-token");
        });
        Assertions.assertEquals(responseException.statusCode(), 401);
        Assertions.assertEquals(responseException.getMessage(), "Error: unauthorized");

        responseException = Assertions.assertThrows(ResponseException.class, () -> {
            service.createGame(new CreateGameRequest(null), null);
        });
        Assertions.assertEquals(responseException.statusCode(), 400);
        Assertions.assertEquals(responseException.getMessage(), "Error: bad request");
    }

    @Test
    void positiveListGame() throws ResponseException, DataAccessException {
        RegisterResult registerResult = service.register(testRegister);
        Assertions.assertEquals(0, service.listGames(new ListGamesRequest(registerResult.authToken())).games().size());

        service.createGame(new CreateGameRequest("testGameName1"), registerResult.authToken());
        service.createGame(new CreateGameRequest("testGameName2"), registerResult.authToken());
        service.createGame(new CreateGameRequest("testGameName3"), registerResult.authToken());

        ArrayList<ListGameData> testGamesList = service.listGames(new ListGamesRequest(registerResult.authToken())).games();
        Assertions.assertEquals(dataAccess.listGame().size(), testGamesList.size());

        for (int i = 0; i < testGamesList.size(); i++) {
            Assertions.assertEquals(dataAccess.listGame().get(i).gameID(), testGamesList.get(i).gameID());
            Assertions.assertEquals(dataAccess.listGame().get(i).blackUsername(), testGamesList.get(i).blackUsername());
            Assertions.assertEquals(dataAccess.listGame().get(i).whiteUsername(), testGamesList.get(i).whiteUsername());
            Assertions.assertEquals(dataAccess.listGame().get(i).gameName(), testGamesList.get(i).gameName());
        }
    }

    @Test
    void negativeListGame() throws ResponseException, DataAccessException {
        ResponseException responseException = Assertions.assertThrows(ResponseException.class, () -> {
            service.listGames(new ListGamesRequest("bad-auth-token"));
        });
        Assertions.assertEquals(responseException.statusCode(), 401);
        Assertions.assertEquals(responseException.getMessage(), "Error: unauthorized");
    }

    @Test
    void positiveJoinGame() throws ResponseException, DataAccessException {
        RegisterResult registerResult = service.register(testRegister);
        CreateGameResult createGameResult = service.createGame(new CreateGameRequest("testGameName"), registerResult.authToken());

        service.joinGame(new JoinGameRequest("WHITE", createGameResult.gameID()), registerResult.authToken());
        Assertions.assertEquals(dataAccess.getGame(createGameResult.gameID()).whiteUsername(), testUsername);

        service.joinGame(new JoinGameRequest("BLACK", createGameResult.gameID()), registerResult.authToken());
        Assertions.assertEquals(dataAccess.getGame(createGameResult.gameID()).whiteUsername(), testUsername);
    }

    @Test
    void negativeJoinGame() throws ResponseException, DataAccessException {
        RegisterResult registerResult = service.register(testRegister);
        CreateGameResult createGameResult = service.createGame(new CreateGameRequest("testGameName"), registerResult.authToken());
        ResponseException responseException = Assertions.assertThrows(ResponseException.class, () -> {
            service.joinGame(new JoinGameRequest("WHITE", createGameResult.gameID()), "bad-auth-token");
        });
        Assertions.assertEquals(responseException.statusCode(), 401);
        Assertions.assertEquals(responseException.getMessage(), "Error: unauthorized");

        // Bad gameID
        responseException = Assertions.assertThrows(ResponseException.class, () -> {
            service.joinGame(new JoinGameRequest("WHITE", 100000), registerResult.authToken());
        });
        Assertions.assertEquals(responseException.statusCode(), 500);

        // Color not White or Black
        responseException = Assertions.assertThrows(ResponseException.class, () -> {
            service.joinGame(new JoinGameRequest("CrazyColor", createGameResult.gameID()), registerResult.authToken());
        });
        Assertions.assertEquals(responseException.statusCode(), 400);
        Assertions.assertEquals(responseException.getMessage(), "Error: bad request");

        // Game spot is already taken
        service.joinGame(new JoinGameRequest("WHITE", createGameResult.gameID()), registerResult.authToken());
        responseException = Assertions.assertThrows(ResponseException.class, () -> {
            service.joinGame(new JoinGameRequest("WHITE", createGameResult.gameID()), registerResult.authToken());
        });
        Assertions.assertEquals(responseException.statusCode(), 403);
        Assertions.assertEquals(responseException.getMessage(), "Error: already taken");
    }
}
