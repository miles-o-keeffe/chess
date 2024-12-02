package client;

import chess.ChessGame;
import exception.ResponseException;
import request.*;
import result.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ClientLoggedIn {
    private final String serverURL;
    private final ServerFacade serverFacade;
    private final String currentAuthToken;
    private ChessGame.TeamColor teamColor;
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
                case "logout" -> logout();
                // case "quit" -> logout();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            try {
                CreateGameResult createGameResult = serverFacade.createGame(new CreateGameRequest(params[0]), currentAuthToken);
                return String.format("Game \"" + params[0] + "\" created", createGameResult.gameID());
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

    public String listGames(String... params) throws ResponseException {
        if (params.length >= 1) {
            throw new ResponseException(400, "Expected no parameters");
        }
        try {
            ListGamesResult listGamesResult = serverFacade.listGames(currentAuthToken);
            this.recentGameList = listGamesResult.games();

            for (int i = 0; i < listGamesResult.games().size(); i++) {
                ListGameData currentListGameData = listGamesResult.games().get(i);
                System.out.println(i + 1 + ".   Game Name: " + currentListGameData.gameName() + ", White: "
                        + currentListGameData.whiteUsername() + ", Black: " + currentListGameData.blackUsername());
            }
            return "";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String joinGame(String... params) throws ResponseException {
        if (recentGameList == null) {
            return "You must call list games before joining a game";
        }

        if (params.length >= 2) {
            try {

                if (!params[0].matches("-?\\d+")) {
                    return "<ID> must be a number";
                }

                if (Integer.parseInt(params[0]) > recentGameList.size() || Integer.parseInt(params[0]) < 1) {
                    return "<ID> must be from the list of games";
                }

                int mySQLGameID = recentGameList.get(Integer.parseInt(params[0]) - 1).gameID();
                serverFacade.joinGame(new JoinGameRequest(params[1], mySQLGameID), currentAuthToken);
                this.setGameJoinedID(mySQLGameID);

                // Sets the team color
                if (Objects.equals(params[1].toUpperCase(), "WHITE")) {
                    this.teamColor = ChessGame.TeamColor.WHITE;
                } else {
                    this.teamColor = ChessGame.TeamColor.BLACK;
                }

                return String.format("Game \"" + params[0] + "\" joined%n");
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
    }

    public String observeGame(String... params) throws ResponseException {
        if (recentGameList == null) {
            return "You must call list games before observing a game";
        }

        if (params.length >= 1) {
            try {

                if (!params[0].matches("-?\\d+")) {
                    return "<ID> must be a number";
                }

                if (Integer.parseInt(params[0]) > recentGameList.size() || Integer.parseInt(params[0]) < 1) {
                    return "<ID> must be from the list of games";
                }

                int mySQLGameID = recentGameList.get(Integer.parseInt(params[0]) - 1).gameID();
                this.setGameJoinedID(mySQLGameID);
                this.setObserving(true);
                this.teamColor = ChessGame.TeamColor.WHITE;
                return String.format("Game \"" + params[0] + "\" joined as an observer");
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        throw new ResponseException(400, "Expected: <ID>");
    }

    public String logout() throws ResponseException {
        try {
            LogoutResult logoutResult = serverFacade.logout(new LogoutRequest(currentAuthToken));
        } catch (Exception e) {
            return e.getMessage();
        }
        return "logging out...";
    }

    public String help() {
        return "create <NAME> - a game\n" +
                "list - games\n" +
                "join <ID> [WHITE|BLACK] - a game\n" +
                "observe <ID> - a game\n" +
                "logout - when you are done\n" +
                // "quit - playing chess\n" +
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


    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }


}
