package client;

import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
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

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    void positiveLogin() throws Exception {

    }

    @Test
    void negativeLogin() throws Exception {

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
        RegisterResult registerResult = facade.register(registerRequest);
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
