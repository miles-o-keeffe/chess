package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import request.CreateGameRequest;

import java.util.ArrayList;

public class MySQLDataAccessTest {
    private static DataAccess dataAccess;
    private final String testUsername = "test_username";
    private final String testPassword = "test_password";
    private final String testEmail = "test_email";

    @BeforeAll
    static void createDatabase() throws ResponseException, DataAccessException {
        dataAccess = new MySqlDataAccess();
    }

    @BeforeEach
    void clear() throws DataAccessException {
        dataAccess.clear();
    }

    @Test
    void clearTest() throws DataAccessException {
        // Adds data to all the database tables and then clears them
        for (int i = 1; i < 11; i++) {
            UserData testUserData = new UserData(testUsername + (i),
                    testPassword + (i), testEmail + (i));
            dataAccess.createUser(testUserData);
            String testGameName = "test_game";
            dataAccess.createGame(testGameName + ((char) i));
        }

        dataAccess.clear();

        Assertions.assertTrue(dataAccess.isEmpty());
    }

    @Test
    void positiveCreateUser() throws DataAccessException {
        UserData testUserData = new UserData(testUsername,
                testPassword, testEmail);

        dataAccess.createUser(testUserData);

        Assertions.assertEquals(testUserData.username(), dataAccess.getUser(testUsername).username());
        Assertions.assertTrue(BCrypt.checkpw(testUserData.password(), dataAccess.getUser(testUsername).password()));
        Assertions.assertEquals(testUserData.email(), dataAccess.getUser(testUsername).email());
    }

    @Test
    void negativeCreateUser() throws DataAccessException {
        UserData testUserData = new UserData(null,
                null, null);

        Assertions.assertThrows(DataAccessException.class, () -> {
            dataAccess.createUser(testUserData);
        });

        UserData testUserData2 = new UserData(testUsername,
                testPassword, testEmail);
        dataAccess.createUser(testUserData2);
        Assertions.assertNull(dataAccess.createUser(testUserData2));
    }

    @Test
    void positiveGetUser() throws DataAccessException {
        positiveCreateUser(); // These tests are the same

        Assertions.assertNull(dataAccess.getUser(null));

        Assertions.assertNull(dataAccess.getUser("non_existent_username"));
    }

    @Test
    void negativeGetUser() throws DataAccessException {
        // The way my getUser is written there isn't really a negative case
        Assertions.assertNull(dataAccess.getUser(null));
    }

    @Test
    void positiveCreateGame() throws DataAccessException {
        int gameID = dataAccess.createGame("testGameName1");
        GameData gameData = new GameData(gameID, null, null,
                "testGameName1", new ChessGame());

        Assertions.assertEquals(gameData, dataAccess.getGame(gameID));
    }

    @Test
    void negativeCreateGame() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> {
            dataAccess.createGame(null);
        });
    }

    @Test
    void positiveGetGame() throws DataAccessException {
        int gameID = dataAccess.createGame("test_game");

        GameData expectedGame = new GameData(gameID, null, null, "test_game", new ChessGame());
        Assertions.assertEquals(dataAccess.getGame(gameID), expectedGame);
    }

    @Test
    void negativeGetGame() throws DataAccessException {
        // The way my getGame is written there isn't really a negative case
        Assertions.assertNull(dataAccess.getGame(-1));
    }

    @Test
    void positiveListGame() throws DataAccessException {
        Assertions.assertEquals(new ArrayList<GameData>(), dataAccess.listGame());

        String[] gameNameList = {"testGameName1", "testGameName2", "testGameName3"};

        ArrayList<GameData> gameList = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            gameList.add(new GameData(i, null, null,
                    gameNameList[i - 1], new ChessGame()));
            dataAccess.createGame(gameNameList[i - 1]);
        }

        ArrayList<GameData> returnedList = dataAccess.listGame();

        Assertions.assertEquals(gameList, returnedList);
    }

    @Test
    void negativeListGame() throws DataAccessException {
        // The way my list games is written, there really isn't a negative case
        Assertions.assertEquals(new ArrayList<GameData>(), dataAccess.listGame());
    }

    @Test
    void positiveUpdateGame() throws DataAccessException, InvalidMoveException {
        int gameID = dataAccess.createGame("testGameName1");

        // Tests adding users
        GameData gameData = new GameData(gameID, testUsername, testUsername + "2",
                "testGameName1", new ChessGame());

        dataAccess.updateGame(gameData);
        Assertions.assertEquals(gameData, dataAccess.getGame(gameID));

        // Tests a chess move
        ChessGame chessGame = new ChessGame();
        chessGame.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
        GameData gameData2 = new GameData(gameID, testUsername + "3", testUsername + "2",
                "testGameName1", chessGame);

        dataAccess.updateGame(gameData2);
        Assertions.assertEquals(gameData2, dataAccess.getGame(gameID));
    }

    @Test
    void negativeUpdateGame() throws DataAccessException {
        int gameID = dataAccess.createGame("testGameName1");

        GameData nullGameData = new GameData(gameID, null, null, null, null);

        // Tries to add null data
        Assertions.assertThrows(DataAccessException.class, () -> {
            dataAccess.updateGame(nullGameData);
        });
    }

    @Test
    void positiveCreateAuth() throws DataAccessException {
        AuthData returnedAuthData = dataAccess.createAuth(testUsername);

        Assertions.assertEquals(returnedAuthData, dataAccess.getAuth(returnedAuthData.authToken()));
        Assertions.assertEquals(returnedAuthData, dataAccess.getAuthByUsername(returnedAuthData.username()));
    }

    @Test
    void negativeCreateAuth() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> {
            AuthData returnedAuthData = dataAccess.createAuth(null);
        });
    }

    @Test
    void positiveGetAuth() throws DataAccessException {
        AuthData returnedAuthData = dataAccess.createAuth(testUsername);

        Assertions.assertEquals(returnedAuthData, dataAccess.getAuth(returnedAuthData.authToken()));
    }

    @Test
    void negativeGetAuth() throws DataAccessException {
        // My getAuth doesn't really have a negative case
        Assertions.assertNull(dataAccess.getAuth(null));
        Assertions.assertNull(dataAccess.getAuth("random_auth"));
    }

    @Test
    void positiveGetAuthByUsername() throws DataAccessException {
        AuthData returnedAuthData = dataAccess.createAuth(testUsername);

        Assertions.assertEquals(returnedAuthData, dataAccess.getAuthByUsername(returnedAuthData.username()));
    }

    @Test
    void negativeGetAuthByUsername() throws DataAccessException {
        // My getAuthByUsername doesn't really have a negative case
        Assertions.assertNull(dataAccess.getAuth(null));
        Assertions.assertNull(dataAccess.getAuth("random_username"));
    }

    @Test
    void positiveDeleteAuth() throws DataAccessException {
        AuthData returnedAuthData = dataAccess.createAuth(testUsername);

        dataAccess.deleteAuth(returnedAuthData);

        Assertions.assertNull(dataAccess.getAuth(returnedAuthData.authToken()));
    }

    @Test
    void negativeDeleteAuth() throws DataAccessException {
        // My deleteAuth doesn't really have a negative case
        dataAccess.deleteAuth(new AuthData(null, null));
        Assertions.assertNull(dataAccess.getAuth(null));
    }

}
