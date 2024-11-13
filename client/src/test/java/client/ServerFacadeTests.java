package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import request.*;
import result.*;
import server.Server;

import java.util.ArrayList;
import java.util.Random;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private String authtoken;
    private final String auth_username = "auth_player";

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterEach
    public void clear() throws ResponseException {
        facade.clear();
    }

    @BeforeEach
    public void generateAuthToken() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest(auth_username, "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);

        this.authtoken = registerResult.authToken();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void clearTest() throws ResponseException {
        facade.clear();
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

        Assertions.assertThrows(Exception.class, () -> {
            facade.createGame(createGameRequestNull, "Bogus_Auth");
        });
    }

    @Test
    void positiveListGame() throws Exception {
        ListGamesResult listGamesResultEmpty = facade.listGames(this.authtoken);
        Assertions.assertEquals(listGamesResultEmpty.games(), new ArrayList<ListGameData>());

        for (int i = 0; i < 5; i++) {
            CreateGameRequest createGameRequest = new CreateGameRequest("test_game_name" + i);
            facade.createGame(createGameRequest, this.authtoken);
        }

        ListGamesResult listGamesResult = facade.listGames(this.authtoken);

        for (int i = 0; i < 5; i++) {
            Assertions.assertEquals(listGamesResult.games().get(i).gameName(), "test_game_name" + i);
            Assertions.assertEquals(listGamesResult.games().get(i).gameID(), i + 1);
        }
    }

    @Test
    void negativeListGame() throws Exception {
        Assertions.assertThrows(Exception.class, () -> {
            facade.listGames("Bogus_Auth");
        });
    }

    @Test
    void positiveJoinGame() throws Exception {
        CreateGameRequest createGameRequest = new CreateGameRequest("test_game_name");
        CreateGameResult createGameResult = facade.createGame(createGameRequest, this.authtoken);

        JoinGameRequest joinGameRequestWHITE = new JoinGameRequest("WHITE", createGameResult.gameID());
        facade.joinGame(joinGameRequestWHITE, this.authtoken);

        ListGamesResult listGamesResultWhite = facade.listGames(this.authtoken);
        Assertions.assertEquals(listGamesResultWhite.games().getFirst().whiteUsername(), this.auth_username);

        JoinGameRequest joinGameRequestBlack = new JoinGameRequest("BLACK", createGameResult.gameID());
        facade.joinGame(joinGameRequestBlack, this.authtoken);

        ListGamesResult listGamesResultBlack = facade.listGames(this.authtoken);
        Assertions.assertEquals(listGamesResultBlack.games().getFirst().blackUsername(), this.auth_username);
    }

    @Test
    void negativeJoinGame() throws Exception {
        CreateGameRequest createGameRequest = new CreateGameRequest("test_game_name");
        CreateGameResult createGameResult = facade.createGame(createGameRequest, this.authtoken);

        JoinGameRequest joinGameRequestWHITE = new JoinGameRequest("WHITE", createGameResult.gameID());
        facade.joinGame(joinGameRequestWHITE, this.authtoken);
        Assertions.assertThrows(Exception.class, () -> {
            facade.joinGame(joinGameRequestWHITE, this.authtoken);
        });


        JoinGameRequest joinGameRequestBadID = new JoinGameRequest("WHITE", 9999);
        Assertions.assertThrows(Exception.class, () -> {
            facade.joinGame(joinGameRequestBadID, this.authtoken);
        });

        Assertions.assertThrows(Exception.class, () -> {
            facade.joinGame(joinGameRequestWHITE, "BOGUS_AUTH");
        });
    }

    @Test
    void positiveLogout() throws Exception {
        LoginRequest loginRequest = new LoginRequest(auth_username, "password");
        LoginResult loginResult = facade.login(loginRequest);

        // Makes sure LogoutResult returns properly
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        LogoutResult logoutResult = facade.logout(logoutRequest);
        Assertions.assertEquals(logoutResult, new LogoutResult());
    }

    @Test
    void negativeLogout() throws Exception {
        LogoutRequest logoutRequestBogusAuth = new LogoutRequest("Bogus_Auth");
        Assertions.assertThrows(Exception.class, () -> {
            facade.logout(logoutRequestBogusAuth);
        });
    }

}
