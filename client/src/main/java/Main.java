import chess.*;
import client.ReplPreLogin;

public class Main {
    public static void main(String[] args) {

        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new ReplPreLogin(serverUrl).run();
    }
}