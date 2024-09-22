package chess.PieceRules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopRule {

    private static final int[][] movementArray = new int[][]{{1, -1}, {-1, 1}, {-1, -1}, {1, 1}};

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int checkRow;
        int checkCol;
        ChessGame.TeamColor teamColor = board.getPiece(myPosition).getTeamColor();
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        for (int[] moves : movementArray) {
            checkRow = row;
            checkCol = col;
            while (true) {
                checkRow = checkRow + moves[0];
                checkCol = checkCol + moves[1];
                if (checkRow > 0 && checkRow < 9 && checkCol > 0 && checkCol < 9) {
                    if (board.getPiece(new ChessPosition(checkRow, checkCol)) == null) {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                    } else if (board.getPiece(new ChessPosition(checkRow, checkCol)).getTeamColor() != teamColor) {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                        break;
                    } else if (board.getPiece(new ChessPosition(checkRow, checkCol)).getTeamColor() == teamColor) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return validMoves;
    }
}
