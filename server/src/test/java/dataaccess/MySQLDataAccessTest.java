package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.RegisterRequest;

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
    void negativeCreateGame() {

    }

    @Test
    void positiveGetGame() throws DataAccessException {
        int gameID = dataAccess.createGame("test_game");

        GameData expectedGame = new GameData(gameID, null, null, "test_game", new ChessGame());
        Assertions.assertEquals(dataAccess.getGame(gameID), expectedGame);
    }

    @Test
    void negativeGetGame() {

    }

    @Test
    void positiveListGame() {

    }

    @Test
    void negativeListGame() {

    }

    @Test
    void positiveUpdateGame() {

    }

    @Test
    void negativeUpdateGame() {

    }

    @Test
    void positiveCreateAuth() {

    }

    @Test
    void negativeCreateAuth() {

    }

    @Test
    void positiveGetAuth() {

    }

    @Test
    void negativeGetAuth() {

    }

    @Test
    void positiveGetAuthByUsername() {

    }

    @Test
    void negativeGetAuthByUsername() {

    }

    @Test
    void positiveDeleteAuth() {

    }

    @Test
    void negativeDeleteAuth() {

    }

}
