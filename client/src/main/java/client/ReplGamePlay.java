package client;

import chess.ChessBoard;
import client.websocket.MessageHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class ReplGamePlay implements MessageHandler {
    private ClientGamePlay client;

    public ReplGamePlay(String serverUrl, String currentAuthToken, int gameID) {
        this.client = new ClientGamePlay(serverUrl, currentAuthToken, gameID);
    }

    public void run() {
        System.out.println();
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        client.drawGame(chessBoard);

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
    public void errorMessageNotify(ErrorMessage errorMsg) {
        System.out.print(errorMsg.getErrorMessage());
    }

    @Override
    public void notificationMessageNotify(NotificationMessage notificationMsg) {
        System.out.print(notificationMsg.getMessage());
    }

    @Override
    public void loadGameMessageNotify(LoadGameMessage loadGameMsg) {
        System.out.print("Game to print");
    }
}
