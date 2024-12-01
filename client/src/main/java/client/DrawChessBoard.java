package client;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ui.EscapeSequences.*;

public class DrawChessBoard {
    private final Map<ChessPiece.PieceType, String> pieceTypeMap = new HashMap<>();
    private final char[] columnLetters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

    public DrawChessBoard() {
        pieceTypeMap.put(ChessPiece.PieceType.KING, "K");
        pieceTypeMap.put(ChessPiece.PieceType.QUEEN, "Q");
        pieceTypeMap.put(ChessPiece.PieceType.BISHOP, "B");
        pieceTypeMap.put(ChessPiece.PieceType.KNIGHT, "K");
        pieceTypeMap.put(ChessPiece.PieceType.ROOK, "R");
        pieceTypeMap.put(ChessPiece.PieceType.PAWN, "P");
    }

    public enum BoardOrientation {
        WHITE,
        BLACK
    }

    public void drawBoard(ChessBoard chessBoard, ChessGame.TeamColor teamColor, Collection<ChessMove> validMoves, ChessPosition piecePosition) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        BoardOrientation orientation;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            orientation = BoardOrientation.WHITE;
        } else {
            orientation = BoardOrientation.BLACK;
        }

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                drawBorder(r, c, out, orientation, validMoves);

                drawPieces(r, c, out, orientation, chessBoard);

                if (c == 9) {
                    out.println(RESET_BG_COLOR);
                }
            }
        }
    }

    public void drawBorder(int row, int col, PrintStream out, BoardOrientation orientation, Collection<ChessMove> validMoves) {

        // Sets border color
        if (row == 0 || row == 9 || col == 0 || col == 9) {
            out.print(SET_BG_COLOR_DARK_GREY);
        } else {
            if (validMoves != null && !validMoves.isEmpty()) {
                drawHighlights(row, col, out, orientation, validMoves);
            } else {
                if ((col + row) % 2 == 0) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                } else {
                    out.print(SET_BG_COLOR_BLACK);
                }
            }
        }

        if (row == 0 && (col == 0 || col == 9) || (row == 9 && (col == 0 || col == 9))) {
            out.print(EMPTY);
        }

        // Prints column letters
        if ((row == 0 || row == 9) && (col < 9 && col > 0)) {
            out.print(SET_TEXT_COLOR_WHITE);
            if (orientation == BoardOrientation.BLACK) {
                out.print(" " + columnLetters[(col - 1) * -1 + 7] + " ");
            } else {
                out.print(" " + columnLetters[col - 1] + " ");
            }
        }

        // Prints row numbers
        if ((col == 0 || col == 9) && (row < 9 && row > 0)) {
            out.print(SET_TEXT_COLOR_WHITE);
            if (orientation == BoardOrientation.BLACK) {
                out.print(" " + row + " ");
            } else {
                out.print(" " + ((row * -1) + 9) + " ");
            }
        }
    }

    public void drawPieces(int row, int col, PrintStream out, BoardOrientation orientation, ChessBoard chessBoard) {
        if ((col < 9 && col > 0) && (row < 9 && row > 0)) {
            String pieceToPrint = "";
            ChessPiece currPiece;
            if (orientation == BoardOrientation.BLACK) {
                currPiece = chessBoard.getPiece(new ChessPosition(row, (col * -1) + 9));
            } else {
                currPiece = chessBoard.getPiece(new ChessPosition((row * -1) + 9, col));
            }

            if (currPiece != null) {
                pieceToPrint = pieceTypeMap.get(currPiece.getPieceType());
                if (currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    out.print(SET_TEXT_COLOR_RED);
                } else {
                    out.print(SET_TEXT_COLOR_BLUE);
                }
                out.print(" " + pieceToPrint + " ");
            } else {
                out.print(EMPTY);
            }
        }
    }

    private void drawHighlights(int row, int col, PrintStream out, BoardOrientation orientation,
                                Collection<ChessMove> validMoves) {
        if ((col < 9 && col > 0) && (row < 9 && row > 0)) {
            if (orientation == BoardOrientation.BLACK) {
                col = (col - 1) * -1 + 8;
            } else {
                row = (row * -1) + 9;
            }
            for (ChessMove move : validMoves) {
                if (move.getStartPosition().getRow() == row && move.getStartPosition().getColumn() == col) {
                    out.print(SET_BG_COLOR_YELLOW);
                    return;
                } else if (move.getEndPosition().getRow() == row && move.getEndPosition().getColumn() == col) {
                    if ((col + row) % 2 == 0) {
                        out.print(SET_BG_COLOR_GREEN);
                        return;
                    } else {
                        out.print(SET_BG_COLOR_DARK_GREEN);
                        return;
                    }
                } else {
                    if ((col + row) % 2 == 0) {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                    } else {
                        out.print(SET_BG_COLOR_BLACK);
                    }
                }
            }
        }
    }

    public void drawBreakBlack() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(SET_BG_COLOR_BLACK);
        for (int i = 0; i < 10; i++) {
            System.out.print("   ");
        }
        System.out.println(RESET_BG_COLOR);
    }
}
