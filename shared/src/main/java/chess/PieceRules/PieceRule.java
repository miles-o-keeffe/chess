package chess.PieceRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

/**
 * This abstract class show what a PieceRule class should have:
 * A two-dimensional array of possible moves
 * A pieceMoves class to calculate moves
 */
abstract class PieceRule {
    int[][] movementArray;

    abstract public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
