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

            DataAccess dataAccess = new MySqlDataAccess();
            if (args.length >= 1 && Objects.equals(args[0], "mem")) {
                dataAccess = new MemoryDataAccess();
            }

            Server server = new Server(dataAccess);
            server.run(port);
            System.out.printf("Server started on port %d%n", port);
            System.out.println("♕ 240 Chess Server");
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}