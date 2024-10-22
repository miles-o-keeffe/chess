package chess.piecerules;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnRule {
    private static final int[][] movementArray = new int[][]{{1, 0}, {1, -1}, {1, 1}};

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessGame.TeamColor teamColor = board.getPiece(myPosition).getTeamColor();

        if (teamColor == ChessGame.TeamColor.BLACK) {
            validMoves = pieceMovesBlack(board, myPosition, validMoves, teamColor);
        } else {
            validMoves = pieceMovesWhite(board, myPosition, validMoves, teamColor);
        }

        return validMoves;
    }

    private static ArrayList<ChessMove> pieceMovesBlack(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> validMoves, ChessGame.TeamColor teamColor) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int checkRow;
        int checkCol;

        for (int[] moves : movementArray) {
            checkRow = row;
            checkCol = col;
            checkRow = checkRow - moves[0];
            checkCol = checkCol - moves[1];
            if (checkRow > 0 && checkRow < 9 && checkCol > 0 && checkCol < 9) {
                if (board.getPiece(new ChessPosition(checkRow, checkCol)) == null && moves[1] == 0 && row == 7) {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                    if (board.getPiece(new ChessPosition(checkRow - 1, checkCol)) == null) {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow - 1, checkCol), null));
                    }
                } else if (board.getPiece(new ChessPosition(checkRow, checkCol)) == null && moves[1] == 0) {
                    if (checkRow == 1) {
                        validMoves = addPromotionMoves(myPosition, new ChessPosition(checkRow, checkCol), validMoves);
                    } else {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                    }
                } else if (board.getPiece(new ChessPosition(checkRow, checkCol)) != null) {
                    if (board.getPiece(new ChessPosition(checkRow, checkCol)).getTeamColor() != teamColor && moves[1] != 0) {
                        if (checkRow == 1) {
                            validMoves = addPromotionMoves(myPosition, new ChessPosition(checkRow, checkCol), validMoves);
                        } else {
                            validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                        }
                    }
                }
            }
        }

        return validMoves;
    }

    private static ArrayList<ChessMove> pieceMovesWhite(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> validMoves, ChessGame.TeamColor teamColor) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int checkRow;
        int checkCol;

        for (int[] moves : movementArray) {
            checkRow = row;
            checkCol = col;
            checkRow = checkRow + moves[0];
            checkCol = checkCol + moves[1];
            if (checkRow > 0 && checkRow < 9 && checkCol > 0 && checkCol < 9) {
                if (board.getPiece(new ChessPosition(checkRow, checkCol)) == null && moves[1] == 0 && row == 2) {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                    if (board.getPiece(new ChessPosition(checkRow + 1, checkCol)) == null) {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow + 1, checkCol), null));
                    }
                } else if (board.getPiece(new ChessPosition(checkRow, checkCol)) == null && moves[1] == 0) {
                    if (checkRow == 8) {
                        validMoves = addPromotionMoves(myPosition, new ChessPosition(checkRow, checkCol), validMoves);
                    } else {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                    }
                } else if (board.getPiece(new ChessPosition(checkRow, checkCol)) != null) {
                    if (board.getPiece(new ChessPosition(checkRow, checkCol)).getTeamColor() != teamColor && moves[1] != 0) {
                        if (checkRow == 8) {
                            validMoves = addPromotionMoves(myPosition, new ChessPosition(checkRow, checkCol), validMoves);
                        } else {
                            validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                        }
                    }
                }
            }
        }

        return validMoves;
    }

    private static ArrayList<ChessMove> addPromotionMoves(ChessPosition myPos, ChessPosition newPos, ArrayList<ChessMove> validMoves) {
        validMoves.add(new ChessMove(myPos, newPos, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(myPos, newPos, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(myPos, newPos, ChessPiece.PieceType.KNIGHT));
        validMoves.add(new ChessMove(myPos, newPos, ChessPiece.PieceType.BISHOP));
        return validMoves;
    }
}
