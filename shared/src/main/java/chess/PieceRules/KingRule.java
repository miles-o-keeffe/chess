package chess.PieceRules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingRule extends PieceRule {

    public KingRule() {
        this.movementArray = new int[][]{{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int checkRow;
        int checkCol;
        ChessGame.TeamColor teamColor = board.getPiece(myPosition).getTeamColor();
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        for (int[] moves : movementArray) {
            checkRow = row + moves[0];
            checkCol = col + moves[1];
            if (checkRow > 0 && checkRow < 9 && checkCol > 0 && checkCol < 9) {
                if (board.getPiece(new ChessPosition(checkRow, checkCol)) == null) {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                } else if (board.getPiece(new ChessPosition(checkRow, checkCol)).getTeamColor() != teamColor) {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                }
            }
        }

        return validMoves;
    }
}
