package client;

import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import org.junit.jupiter.api.*;
import request.CreateGameRequest;
import request.ListGamesRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.*;
import server.Server;

import java.util.Objects;
import java.util.Random;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private String authtoken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clear() {
//        var path = "/db";
//        return this.makeRequest("DELETE", path, request, LogoutResult.class, request.authToken());
    }

    @BeforeEach
    public void generateAuthToken() throws ResponseException {
        Random random = new Random();
        int randomNumber = random.nextInt(10000) + 1;
        RegisterRequest registerRequest = new RegisterRequest("player" + randomNumber, "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);

        this.authtoken = registerResult.authToken();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void positiveLogin() throws Exception {
        Random random = new Random();
        int randomNumber = random.nextInt(10000) + 1;
        RegisterRequest registerRequest = new RegisterRequest("player" + randomNumber, "password", "p1@email.com");
        facade.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("player" + randomNumber, "password");
        LoginResult loginResult = facade.login(loginRequest);
        Assertions.assertTrue(loginResult.authToken().length() > 10);
        Assertions.assertEquals(loginResult.username(), loginRequest.username());
    }

    @Test
    void negativeLogin() throws Exception {
        // Username does not exists
        LoginRequest usernameDNELoginRequest = new LoginRequest("username_dne", "password");
        Assertions.assertThrows(Exception.class, () -> {
            facade.login(usernameDNELoginRequest);
        });

        Random random = new Random();
        int randomNumber = random.nextInt(10000) + 1;
        RegisterRequest registerRequest = new RegisterRequest("player" + randomNumber, "password", "p1@email.com");
        facade.register(registerRequest);

        // Enters the wrong password
        LoginRequest loginRequestWrongPass = new LoginRequest("player" + randomNumber, "wrong_password");
        Assertions.assertThrows(Exception.class, () -> {
            facade.login(loginRequestWrongPass);
        });

        // Login Empty
        LoginRequest loginRequestEmpty = new LoginRequest("", "");
        Assertions.assertThrows(Exception.class, () -> {
            facade.login(loginRequestEmpty);
        });

        LoginRequest loginRequestNull = new LoginRequest(null, null);
        Assertions.assertThrows(Exception.class, () -> {
            facade.login(loginRequestEmpty);
        });
    }

    @Test
    void positiveRegister() throws Exception {
        Random random = new Random();
        int randomNumber = random.nextInt(10000) + 1;
        RegisterRequest registerRequest = new RegisterRequest("player" + randomNumber, "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        Assertions.assertTrue(registerResult.authToken().length() > 10);
        Assertions.assertEquals(registerResult.username(), registerRequest.username());
    }

    @Test
    void negativeRegister() throws Exception {
        Random random = new Random();
        int randomNumber = random.nextInt(10000) + 1;
        RegisterRequest registerRequest = new RegisterRequest("repeat_player" + randomNumber, "password", "p1@email.com");
        facade.register(registerRequest);
        Assertions.assertThrows(Exception.class, () -> {
            facade.register(registerRequest);
        });
    }

    @Test
    void positiveCreateGame() throws Exception {
        CreateGameRequest createGameRequest = new CreateGameRequest("test_game_name");
        CreateGameResult createGameResult = facade.createGame(createGameRequest, this.authtoken);

        ListGamesRequest listGamesRequest = new ListGamesRequest(this.authtoken);
        ListGamesResult listGamesResult = facade.listGames(listGamesRequest.authToken());
        for (ListGameData listGameData : listGamesResult.games()) {
            if (listGameData.gameID() == createGameResult.gameID()) {
                Assertions.assertEquals(listGameData.gameName(), "test_game_name");
                Assertions.assertEquals(listGameData.gameID(), createGameResult.gameID());
            } else {
                throw new Exception();
            }
        }
    }

    @Test
    void negativeCreateGame() throws Exception {
        CreateGameRequest createGameRequestEmpty = new CreateGameRequest("");
        Assertions.assertThrows(Exception.class, () -> {
            facade.createGame(createGameRequestEmpty, this.authtoken);
        });

        CreateGameRequest createGameRequestNull = new CreateGameRequest(null);
        Assertions.assertThrows(Exception.class, () -> {
            facade.createGame(createGameRequestNull, this.authtoken);
        });
    }

    @Test
    void positiveListGame() throws Exception {

    }

    @Test
    void negativeListGame() throws Exception {

    }

    @Test
    void positiveJoinGame() throws Exception {

    }

    @Test
    void negativeJoinGame() throws Exception {

    }

    @Test
    void positiveLogout() throws Exception {

    }

    @Test
    void negativeLogout() throws Exception {

    }

}
