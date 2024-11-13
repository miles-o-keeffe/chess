package client;

import exception.ResponseException;
import model.GameData;
import request.*;
import result.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ClientLoggedIn {
    private final String serverURL;
    private final ServerFacade serverFacade;
    private final String currentAuthToken;
    private ArrayList<ListGameData> recentGameList;
    private int gameJoinedID = -1;
    private boolean isObserving = false;

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
                case "logout", "quit" -> logout();
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
        ListGamesResult listGamesResult = serverFacade.listGames(currentAuthToken);
        this.recentGameList = listGamesResult.games();

        for (int i = 0; i < listGamesResult.games().size(); i++) {
            ListGameData currentListGameData = listGamesResult.games().get(i);
            System.out.println(i + 1 + ".   Game Name: " + currentListGameData.gameName() + ", White: "
                    + currentListGameData.whiteUsername() + ", Black: " + currentListGameData.blackUsername());
        }
        return "";
    }

    public String joinGame(String... params) throws ResponseException {
        if (recentGameList == null) {
            return "You must call list games before joining a game";
        }
        if (params.length >= 2) {
            int mySQLGameID = recentGameList.get(Integer.parseInt(params[0]) - 1).gameID();
            serverFacade.joinGame(new JoinGameRequest(params[1], mySQLGameID), currentAuthToken);
            this.setGameJoinedID(mySQLGameID);
            return String.format("Game \"" + params[0] + "\" joined%n");
        }
        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
    }

    public String observeGame(String... params) throws ResponseException {
        if (recentGameList == null) {
            return "You must call list games before joining a game";
        }
        if (params.length >= 1) {
            int mySQLGameID = recentGameList.get(Integer.parseInt(params[0]) - 1).gameID();
            this.setGameJoinedID(mySQLGameID);
            this.setObserving(true);
            return String.format("Game \"" + params[0] + "\" joined as an observer");
        }
        throw new ResponseException(400, "Expected: <ID>");
    }

    public String logout() throws ResponseException {
        LogoutResult logoutResult = serverFacade.logout(new LogoutRequest(currentAuthToken));
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

    public int getGameJoinedID() {
        return gameJoinedID;
    }

    public void setGameJoinedID(int gameJoinedID) {
        this.gameJoinedID = gameJoinedID;
    }

    public boolean isObserving() {
        return isObserving;
    }

    public void setObserving(boolean observing) {
        isObserving = observing;
    }

    public String getServerURL() {
        return serverURL;
    }

    public String getCurrentAuthToken() {
        return currentAuthToken;
    }

}
