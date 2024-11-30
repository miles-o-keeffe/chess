package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.MessageHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;

import java.io.IOException;
import java.util.Arrays;

public class ClientGamePlay {
    private final String serverURL;
    private int gameID;
    private String authToken;
    private ChessGame.TeamColor teamColor;
    private final DrawChessBoard chessBoardDrawer = new DrawChessBoard();
    private WebSocketFacade ws;
    private boolean isObserving = false;

    public ClientGamePlay(String serverURL, ChessGame.TeamColor teamColor, int gameID,
                          MessageHandler msgHandler, boolean isObserving, String authToken) throws ResponseException {
        this.serverURL = serverURL;
        this.teamColor = teamColor;
        this.gameID = gameID;
        ws = new WebSocketFacade(this.serverURL, msgHandler);
        this.isObserving = isObserving;
        this.authToken = authToken;

        this.connect();
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

    public void connect() {
        try {
            ws.connect(this.authToken, this.gameID);
        } catch (ResponseException e) {
            System.out.println("Error: Couldn't connect to server");
        }
    }

    public void drawGame(ChessBoard chessBoardToDraw) {
        chessBoardDrawer.drawBoard(chessBoardToDraw, this.teamColor);
    }

//    public void drawGame() {
//        chessBoardDrawer.drawBoard(chessBoardToDraw, this.teamColor);
//    }

    private String redrawBoard() throws ResponseException {

        return "";
    }

    private String leaveGame() {

        return "leave";
    }

    private String makeMove(String... params) {
        if (isObserving) {
            System.out.print("Observers cannot make moves");
        }

        if (params.length >= 2) {
            char startCol = params[0].charAt(0);
            char startRow = params[0].charAt(1);

            char endCol = params[1].charAt(0);
            char endRow = params[1].charAt(1);

            ChessPosition startPosition = validateMove(startRow, startCol);
            ChessPosition endPosition = validateMove(endRow, endCol);
            if (startPosition == null || endPosition == null) {
                return "Invalid Input";
            }

            ChessMove chessMove = new ChessMove(startPosition, endPosition, null);

            try {
                ws.makeMove(this.authToken, this.gameID, chessMove);
            } catch (ResponseException e) {
                System.out.print("Error: Couldn't connect to server");
            }
        }
        return "";
    }

    public ChessPosition validateMove(char row, char col) {
        if (col >= 97 && col <= 104 && Character.isLetter(col)
                && row >= 49 && row <= 56 && Character.isDigit(row)) {
            int rowInt = row - '0';
            int colInt = col - '0' - 48;
            return new ChessPosition(rowInt, colInt);
        }
        return null;
    }

    private String resign() {

        return "resign";
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
