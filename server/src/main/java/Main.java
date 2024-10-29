import chess.*;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import server.Server;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {

        try {
            var port = 8080;

            DataAccess dataAccess = new MemoryDataAccess();
            if (args.length >= 1 && Objects.equals(args[0], "sql")) {
                dataAccess = new MySqlDataAccess();
            }

            Server server = new Server(dataAccess);
            server.run(port);
            System.out.printf("Server started on port %d%n", port);
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }

        System.out.println("♕ 240 Chess Server");
    }
}