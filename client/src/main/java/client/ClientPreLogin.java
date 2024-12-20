package client;

import exception.ResponseException;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.Arrays;

public class ClientPreLogin {
    private final String serverURL;
    private final ServerFacade serverFacade;
    private State state = State.SIGNEDOUT;
    private String currentAuthToken;

    public enum State {
        SIGNEDOUT,
        SIGNEDIN
    }

    public ClientPreLogin(String serverURL) {
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
                case "login" -> this.login(params);
                case "register" -> this.register(params);
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

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            try {
                LoginResult loginResult = serverFacade.login(new LoginRequest(params[0], params[1]));
                this.setState(State.SIGNEDIN);
                this.currentAuthToken = loginResult.authToken();
                return String.format("You signed in as %s.", loginResult.username());
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            try {
                RegisterResult registerResult = serverFacade.register(new RegisterRequest(params[0], params[1], params[2]));
                this.setState(State.SIGNEDIN);
                this.currentAuthToken = registerResult.authToken();
                return String.format("You registered and signed in as %s.", registerResult.username());
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getServerURL() {
        return serverURL;
    }


    public String getCurrentAuthToken() {
        return currentAuthToken;
    }

}
