package client;

import chess.ChessBoard;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class ReplGamePlay {
    private ClientGamePlay client;

    public ReplGamePlay(String serverUrl, String currentAuthToken, int gameID) {
        this.client = new ClientGamePlay(serverUrl, currentAuthToken, gameID);
    }

    public void run() {
        System.out.println();
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        client.drawGame(chessBoard);
    }

    private void printPrompt() {
        System.out.print("\n" + "[GAME_PLAY] " + SET_TEXT_COLOR_RED + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
