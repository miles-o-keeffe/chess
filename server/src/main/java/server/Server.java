package server;

import spark.*;

public class Server {
    // private final DataAccess dataAccess = new Memory;
    // private final Service service = new Service(dataAccess);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.post("/user", this::createUser);
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private String createUser(Request req, Response res) throws Exception {
//        return serializer.toJson(result);
        return "";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
