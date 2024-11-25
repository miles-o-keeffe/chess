package client;

import chess.ChessBoard;
import chess.ChessGame;
import client.websocket.MessageHandler;
import exception.ResponseException;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class ReplGamePlay implements MessageHandler {
    private ClientGamePlay client;

    public ReplGamePlay(String serverUrl, ChessGame.TeamColor teamColor, int gameID, boolean isObserving, String authToken) {
        try {
            this.client = new ClientGamePlay(serverUrl, teamColor, gameID, this, isObserving, authToken);
        } catch (ResponseException e) {
            System.out.print("Unable to establish web socket connect. Error: " + e.getMessage());
        }
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!(result.equals("leave") || result.equals("resign"))) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.print(msg);
            }
        }

        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + "[GAME_PLAY] " + SET_TEXT_COLOR_RED + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void serverMsgRouter(ServerMessage serverMsg) {
        if (serverMsg instanceof LoadGameMessage) {
            loadGameMessageNotify((LoadGameMessage) serverMsg);
        } else if (serverMsg instanceof ErrorMessage) {
            errorMessageNotify((ErrorMessage) serverMsg);
        } else {
            notificationMessageNotify((NotificationMessage) serverMsg);
        }
    }

    @Override
    public void loadGameMessageNotify(LoadGameMessage loadGameMsg) {
        System.out.print("Game to print");
    }

    @Override
    public void errorMessageNotify(ErrorMessage errorMsg) {
        System.out.print(errorMsg.getErrorMessage());
    }

    @Override
    public void notificationMessageNotify(NotificationMessage notificationMsg) {
        System.out.print(notificationMsg.getMessage());
    }
}
