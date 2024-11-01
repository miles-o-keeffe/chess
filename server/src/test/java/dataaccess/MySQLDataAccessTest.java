package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;

public class MySQLDataAccessTest {
    private static DataAccess dataAccess;

    @BeforeAll
    static void createDatabase() throws ResponseException, DataAccessException {
        dataAccess = new MySqlDataAccess();
    }

    @Test
    void clear() {

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
