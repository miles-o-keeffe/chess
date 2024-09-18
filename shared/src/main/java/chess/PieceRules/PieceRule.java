package chess.PieceRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

abstract class PieceRule {
    int[][] movementArray;

    abstract public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
