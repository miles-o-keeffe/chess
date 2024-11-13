package client;

import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import server.Server;

import java.util.Random;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

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

    }

    @Test
    void negativeCreateGame() throws Exception {

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
