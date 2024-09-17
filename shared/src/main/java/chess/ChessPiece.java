package chess;

import chess.PieceRules.BishopRule;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    PieceType type;
    ChessGame.TeamColor pieceColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
       return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
       return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch(this.type) {
            case PieceType.KING:
                break;
            case PieceType.QUEEN:
                // return new QueenRule().pieceMoves(board, myPosition);
                break;
            case PieceType.BISHOP:
                return new BishopRule().pieceMoves(board, myPosition);
            case PieceType.KNIGHT:
                break;
            case PieceType.ROOK:
                break;
            case PieceType.PAWN:
                break;
        }
        return new ArrayList<>();
    }
}
