package client;

import chess.ChessBoard;
import exception.ResponseException;

import java.util.Arrays;

public class ClientGamePlay {
    private final String serverURL;
    private String currentAuthToken;
    private int gameID;
    private final DrawChessBoard chessBoardDrawer = new DrawChessBoard();

    public ClientGamePlay(String serverURL, String currentAuthToken, int gameID) {
        this.serverURL = serverURL;
        this.currentAuthToken = currentAuthToken;
        this.gameID = gameID;
    }

    public String eval(String line) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame();
                case "move" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlightMoves(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public void drawGame(ChessBoard chessBoardToDraw) {
        chessBoardDrawer.drawBoard(chessBoardToDraw, DrawChessBoard.BoardOrientation.BLACK);
        chessBoardDrawer.drawBreakBlack();
        chessBoardDrawer.drawBoard(chessBoardToDraw, DrawChessBoard.BoardOrientation.WHITE);
    }

    private String redrawBoard() throws ResponseException {

        return "";
    }

    private String leaveGame() {

        return "";
    }

    private String makeMove(String... params) {

        return "";
    }

    private String resign() {

        return "";
    }

    private String highlightMoves(String... params) {

        return "";
    }

    public String help() {
        return "redraw - the chess board\n" +
                "leave - the game\n" +
                "move <CHESS MOVE> - to make a chess move. format: e4 e5\n" +
                "resign - from the game\n" +
                "highlight <CHESS POSITION> - to see valid moves. format: e4" +
                "help - with possible commands\n";
    }
}
