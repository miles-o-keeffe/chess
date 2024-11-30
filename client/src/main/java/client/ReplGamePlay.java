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

import static ui.EscapeSequences.*;

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
        System.out.print(SET_TEXT_COLOR_GREEN + "\n" + "[GAME_PLAY] " + SET_TEXT_COLOR_RED + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void loadGameMessageNotify(LoadGameMessage loadGameMsg) {
        System.out.println();
        client.drawGame(loadGameMsg.getGame().game().getBoard());
        client.setRecentChessGame(loadGameMsg.getGame().game());
        printPrompt();
    }

    @Override
    public void errorMessageNotify(ErrorMessage errorMsg) {
        System.out.print("\n" + SET_TEXT_COLOR_RED + errorMsg.getErrorMessage());
        printPrompt();
    }

    @Override
    public void notificationMessageNotify(NotificationMessage notificationMsg) {
        System.out.print("\n" + SET_TEXT_COLOR_BLUE + notificationMsg.getMessage());
        printPrompt();
    }
}
