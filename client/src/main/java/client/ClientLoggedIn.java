package client;

import exception.ResponseException;

import java.util.Arrays;

public class ClientLoggedIn {
    private final String serverURL;
    private final ServerFacade serverFacade;

    public ClientLoggedIn(String serverURL) {
        this.serverURL = serverURL;
        serverFacade = new ServerFacade(serverURL);
    }

    public String eval(String line) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return "register <USERNAME> <PASSWORD> <EMAIL> - to create an account\n" +
                "login <USERNAME> <PASSWORD> - to play chess\n" +
                "quit - playing chess\n" +
                "help - with possible commands";
    }
}
