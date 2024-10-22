package server;

import com.google.gson.Gson;
import exception.ResponseException;
import request.RegisterRequest;
import result.ErrorResult;
import result.RegisterResult;
import service.Service;
import spark.*;

public class Server {
    private final Service service = new Service();

    public Server() {

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();
        Spark.post("/user", this::createUser);
        //  Spark.delete("/clear", this::clear);
        Spark.exception(ResponseException.class, this::responseExceptionHandler);
        Spark.exception(Exception.class, this::generalExceptionHandler);
        Spark.notFound((req, res) -> {
            var serializer = new Gson();
            var msg = String.format("[%s] %s not found", req.requestMethod(), req.pathInfo());
            res.status(404);
            return serializer.toJson(new ResponseException(404, msg));
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object createUser(Request req, Response res) throws ResponseException {
        RegisterRequest newUser = new Gson().fromJson(req.body(), RegisterRequest.class);
        if (newUser.email().isBlank() || newUser.username().isBlank() || newUser.password().isBlank()) {
            return responseExceptionHandler(new ResponseException(400, "Error: bad request"), req, res);
        }
        RegisterResult newAuthData = service.register(newUser);
        return new Gson().toJson(newAuthData);
    }

    private Object clear(Request req, Response res) {
        var serializer = new Gson();
        service.clear();
        return "";
    }

    private Object responseExceptionHandler(ResponseException ex, Request req, Response res) {
        var serializer = new Gson();
        res.type("application/json");
        res.body(serializer.toJson(new ErrorResult(ex.getMessage())));
        res.status(ex.StatusCode());
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
