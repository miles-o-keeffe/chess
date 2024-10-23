package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.CreateGameRequest;
import request.RegisterRequest;

class ServiceTest {
    private static final DataAccess DATA_ACCESS = new MemoryDataAccess();
    private static final Service SERVICE = new Service(DATA_ACCESS);
    private final String testUsername = "test_username";
    private final String testPassword = "test_password";
    private final String testEmail = "test_email";
    private final String testGameName = "test_game";

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
        RegisterRequest testRegister = new RegisterRequest(testUsername, testPassword, testEmail);
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
    void negativeRegister() {
    }

    @Test
    void positiveLogin() {
    }

    @Test
    void negativeLogin() {
    }

    @Test
    void positiveLogout() {
    }

    @Test
    void negativeLogout() {
    }

    @Test
    void positiveListGame() {
    }

    @Test
    void negativeGame() {
    }

    @Test
    void positiveJoinGame() {
    }

    @Test
    void negativeJoinGame() {
    }

    @Test
    void positiveCreateGame() {
    }

    @Test
    void negativeCreateGame() {
    }
}
