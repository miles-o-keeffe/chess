package client;

import exception.ResponseException;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import request.LoginRequest;
import result.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ClientLoggedIn {
    private final String serverURL;
    private final ServerFacade serverFacade;
    private String currentAuthToken;
    private ArrayList<ListGameData> recentGameList;

    public ClientLoggedIn(String serverURL, String currentAuthToken) {
        this.serverURL = serverURL;
        serverFacade = new ServerFacade(serverURL);
        this.currentAuthToken = currentAuthToken;
    }

    public String eval(String line) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            CreateGameResult createGameResult = serverFacade.createGame(new CreateGameRequest(params[0]), currentAuthToken);
            return String.format("Game \"" + params[0] + "\" created", createGameResult.gameID());
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

    public String listGames(String... params) throws ResponseException {
        if (params.length >= 1) {
            throw new ResponseException(400, "Expected no parameters");
        }
        ListGamesResult listGamesResult = serverFacade.listGames(new ListGamesRequest(currentAuthToken), currentAuthToken);
        this.recentGameList = listGamesResult.games();

        for (int i = 0; i < listGamesResult.games().size(); i++) {
            ListGameData currentListGameData = listGamesResult.games().get(i);
            System.out.println(i + 1 + ".   Game Name: " + currentListGameData.gameName() + ", White: "
                    + currentListGameData.whiteUsername() + ", Black: " + currentListGameData.blackUsername());
        }
        return "";
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            JoinGameResult joinGameResult = serverFacade.joinGame(new JoinGameRequest(params[1], Integer.parseInt(params[0])), currentAuthToken);
            return String.format("Game \"" + params[0] + "\" joined");
        }
        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
    }

    public String observeGame(String... params) throws ResponseException {
        return "";
    }

    public String logout() {
        return "quit";
    }

    public String help() {
        return "create <NAME> - a game\n" +
                "list - games\n" +
                "join <ID> [WHITE|BLACK] - a game\n" +
                "observe <ID> - a game\n" +
                "logout - when you are done\n" +
                "quit - playing chess\n" +
                "help - with possible commands";
    }
}
