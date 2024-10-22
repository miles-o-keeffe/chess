package chess.piecerules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightRule {

    private static final int[][] MOVEMENT_ARRAY = new int[][]{{2, -1}, {2, 1}, {-1, 2}, {1, 2}, {-2, -1}, {-2, 1}, {-1, -2}, {1, -2}};

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return OneMoveFinder.findMoves(board, myPosition, MOVEMENT_ARRAY);
    }

}
