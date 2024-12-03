package client;

import chess.*;
import client.websocket.MessageHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class ClientGamePlay {
    private final String serverURL;
    private int gameID;
    private String authToken;
    private ChessGame.TeamColor teamColor;
    private final DrawChessBoard chessBoardDrawer = new DrawChessBoard();
    private WebSocketFacade ws;
    private boolean isObserving;
    private ChessGame recentChessGame;

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
        chessBoardDrawer.drawBoard(chessBoardToDraw, this.teamColor, null, null);
    }

    private String redrawBoard() throws ResponseException {
        chessBoardDrawer.drawBoard(this.recentChessGame.getBoard(), this.teamColor, null, null);
        return "";
    }

    private String leaveGame() {
        try {
            ws.leave(this.authToken, this.gameID);
        } catch (ResponseException e) {
            System.out.println("Error: Couldn't connect to server");
        }
        return "leave";
    }

    private String makeMove(String... params) {
        if (isObserving) {
            System.out.print("Observers cannot make moves");
            return "";
        }

        if (params.length >= 2) {
            ChessPiece.PieceType promotionPiece = null;
            if (params.length >= 3) {
                ChessPiece.PieceType newPromotionPiece = getPromotionPiece(params[2]);
                if (newPromotionPiece != ChessPiece.PieceType.QUEEN &&
                        newPromotionPiece != ChessPiece.PieceType.ROOK &&
                        newPromotionPiece != ChessPiece.PieceType.BISHOP &&
                        newPromotionPiece != ChessPiece.PieceType.KNIGHT) {
                    return "Invalid Promotion Piece";
                }
                promotionPiece = newPromotionPiece;
            }

            char startCol = params[0].charAt(0);
            char startRow = params[0].charAt(1);

            char endCol = params[1].charAt(0);
            char endRow = params[1].charAt(1);

            ChessPosition startPosition = validateMove(startRow, startCol);
            ChessPosition endPosition = validateMove(endRow, endCol);
            if (startPosition == null || endPosition == null) {
                return "Invalid Input";
            }

            ChessMove chessMove = new ChessMove(startPosition, endPosition, promotionPiece);

            try {
                ws.makeMove(this.authToken, this.gameID, chessMove);
            } catch (ResponseException e) {
                System.out.print("Error: Couldn't connect to server");
            }
        }
        return "";
    }

    public ChessPiece.PieceType getPromotionPiece(String piece) {
        return switch (piece.toLowerCase()) {
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
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
        if (!isObserving) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Are you sure you want to resign? (Y/N): ");
            String line = scanner.nextLine();
            if (line.toUpperCase().equals("Y")) {
                try {
                    ws.resign(this.authToken, this.gameID);
                } catch (ResponseException e) {
                    System.out.println("Error: Couldn't connect to server");
                }
            } else {
                return "";
            }
        } else {
            System.out.println("Observers cannot resign");
            return "";
        }
        return "";
    }

    private String highlightMoves(String... params) {
        if (params.length >= 1) {
            char col = params[0].charAt(0);
            char row = params[0].charAt(1);

            ChessPosition position = validateMove(row, col);

            if (position == null) {
                return "Invalid Input";
            }

            Collection<ChessMove> validMoves = this.recentChessGame.validMoves(position);

            this.chessBoardDrawer.drawBoard(this.recentChessGame.getBoard(), this.teamColor, validMoves, position);
        }
        return "";
    }

    public String help() {
        return "redraw - the chess board\n" +
                "leave - the game\n" +
                "move <CHESS MOVE> - to make a chess move. format: e4 e5.\n" +
                "                    A promotion piece can be added if needed\n" +
                "resign - from the game\n" +
                "highlight <CHESS POSITION> - to see valid moves. format: e4\n" +
                "help - with possible commands\n";
    }

    public void setRecentChessGame(ChessGame recentChessGame) {
        this.recentChessGame = recentChessGame;
    }
}
