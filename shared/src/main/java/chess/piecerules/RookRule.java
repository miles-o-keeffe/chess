package chess.piecerules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookRule {

    private static final int[][] MOVEMENT_ARRAY = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return RepeatMoveFinder.findMoves(board, myPosition, MOVEMENT_ARRAY);
    }
}
