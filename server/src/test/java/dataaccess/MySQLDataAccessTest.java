package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
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
        // Adds to all the DATA_ACCESS fields so that they can be cleared
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
    void positiveCreateUser() {

    }

    @Test
    void negativeCreateUser() {

    }

    @Test
    void positiveGetUser() {

    }

    @Test
    void negativeGetUser() {

    }

    @Test
    void positiveCreateGame() {

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
