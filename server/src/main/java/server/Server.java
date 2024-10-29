package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import exception.ResponseException;
import request.*;
import result.*;
import service.Service;
import spark.*;

public class Server {
    private DataAccess dataAccess;
    private final Service service = new Service(dataAccess);

    public Server() {
        this.dataAccess = new MemoryDataAccess();
    }

    public Server(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();
        Spark.post("/user", this::createUser);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(ResponseException.class, this::responseExceptionHandler);
        Spark.exception(Exception.class, this::generalExceptionHandler);
        Spark.notFound((req, res) -> {
            var serializer = new Gson();
            var msg = String.format("[%s] %s not found", req.requestMethod(), req.pathInfo());
            res.status(404);
            res.type("application/json");
            res.body(serializer.toJson(new ErrorResult(msg)));
            return serializer.toJson(new ErrorResult(msg));
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object createUser(Request req, Response res) throws ResponseException, DataAccessException {
        RegisterRequest newUser = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResult newAuthData = service.register(newUser);
        return new Gson().toJson(newAuthData);
    }

    private Object clear(Request req, Response res) throws ResponseException {
        service.clear();
        return new Gson().toJson(new ClearResult());
    }

    private Object login(Request req, Response res) throws ResponseException, DataAccessException {
        LoginRequest loginAttempt = new Gson().fromJson(req.body(), LoginRequest.class);
        if (loginAttempt.username().isBlank() || loginAttempt.password().isBlank()) {
            return responseExceptionHandler(new ResponseException(400, "Error: bad request"), req, res);
        }
        LoginResult newAuthData = service.login(loginAttempt);
        return new Gson().toJson(newAuthData);
    }

    private Object logout(Request req, Response res) throws ResponseException, DataAccessException {
        var authToken = req.headers("Authorization");
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        LogoutResult logoutResult = service.logout(logoutRequest);
        return new Gson().toJson(logoutResult);
    }

    private Object listGames(Request req, Response res) throws ResponseException, DataAccessException {
        var authToken = req.headers("Authorization");
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listOfGames = service.listGames(listGamesRequest);
        return new Gson().toJson(listOfGames);
    }

    private Object createGame(Request req, Response res) throws ResponseException, DataAccessException {
        var authToken = req.headers("Authorization");
        CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        if (createGameRequest.gameName().isBlank()) {
            return responseExceptionHandler(new ResponseException(400, "Error: bad request"), req, res);
        }
        CreateGameResult createGameResult = service.createGame(createGameRequest, authToken);
        return new Gson().toJson(createGameResult);
    }

    private Object joinGame(Request req, Response res) throws ResponseException, DataAccessException {
        var authToken = req.headers("Authorization");
        JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
        if (joinGameRequest.gameID() < 1 || joinGameRequest.playerColor() == null) {
            return responseExceptionHandler(new ResponseException(400, "Error: bad request"), req, res);
        }
        JoinGameResult joinGameResult = service.joinGame(joinGameRequest, authToken);
        return new Gson().toJson(joinGameResult);
    }

    private Object responseExceptionHandler(ResponseException ex, Request req, Response res) {
        var serializer = new Gson();
        res.type("application/json");
        res.body(serializer.toJson(new ErrorResult(ex.getMessage())));
        res.status(ex.statusCode());
        return serializer.toJson(new ErrorResult(ex.getMessage()));
    }

    private Object generalExceptionHandler(Exception ex, Request req, Response res) {
        var serializer = new Gson();
        res.type("application/json");
        res.body(serializer.toJson(new ErrorResult(ex.getMessage())));
        res.status(500);
        return serializer.toJson(new ErrorResult(ex.getMessage()));
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
