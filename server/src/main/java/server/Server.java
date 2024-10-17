package server;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private final UserService userService = new UserService();
    private final GameService gameService = new GameService();

    public Server() {

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();
        Spark.post("/user", this::createUser);
        // Spark.delete("/clear", this::clear);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object createUser(Request req, Response res) throws Exception {
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        AuthData newAuthData = userService.register(newUser);
        var serializer = new Gson();
        return serializer.toJson(newAuthData);
    }

    private void clear() {

    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
