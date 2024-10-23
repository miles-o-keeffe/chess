package chess.piecerules;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnRule {
    private static final int[][] MOVEMENT_ARRAY = new int[][]{{1, 0}, {1, -1}, {1, 1}};

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
        return findPawnMoves(board, myPosition, validMoves, teamColor);
    }

    private static ArrayList<ChessMove> pieceMovesWhite(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> validMoves, ChessGame.TeamColor teamColor) {

        return findPawnMoves(board, myPosition, validMoves, teamColor);
    }

    private static ArrayList<ChessMove> findPawnMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> validMoves, ChessGame.TeamColor teamColor) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int checkRow;
        int checkCol;

        int startingRow = 0;
        int promoteRow = 0;
        int checkPromote = 0;
        if (teamColor == ChessGame.TeamColor.BLACK) {
            startingRow = 7;
            promoteRow = 1;
            checkPromote = -1;
        } else if (teamColor == ChessGame.TeamColor.WHITE) {
            startingRow = 2;
            promoteRow = 8;
            checkPromote = 1;
        }

        for (int[] moves : MOVEMENT_ARRAY) {
            checkRow = row;
            checkCol = col;
            checkRow = checkRow + (checkPromote * moves[0]);
            checkCol = checkCol + (checkPromote * moves[1]);
            validMoves = pawnMoveLogic(board, myPosition, validMoves, teamColor, moves, checkRow, checkCol, row, startingRow, checkPromote, promoteRow);
        }

        return validMoves;
    }

    private static ArrayList<ChessMove> pawnMoveLogic(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> validMoves, ChessGame.TeamColor teamColor, int[] moves, int checkRow, int checkCol, int row, int startingRow, int checkPromote, int promoteRow) {
        if (checkRow > 0 && checkRow < 9 && checkCol > 0 && checkCol < 9) {
            var checkPiece = board.getPiece(new ChessPosition(checkRow, checkCol));
            if (checkPiece == null && moves[1] == 0 && row == startingRow) {
                validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                if (board.getPiece(new ChessPosition(checkRow + checkPromote, checkCol)) == null) {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow + checkPromote, checkCol), null));
                }
            } else if (checkPiece == null && moves[1] == 0) {
                if (checkRow == promoteRow) {
                    validMoves = addPromotionMoves(myPosition, new ChessPosition(checkRow, checkCol), validMoves);
                } else {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                }
            } else if (checkPiece != null) {
                if (checkPiece.getTeamColor() != teamColor && moves[1] != 0) {
                    if (checkRow == promoteRow) {
                        validMoves = addPromotionMoves(myPosition, new ChessPosition(checkRow, checkCol), validMoves);
                    } else {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
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
