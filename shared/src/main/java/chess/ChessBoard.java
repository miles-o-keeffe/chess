package chess;

import chess.PieceRules.PawnRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {

    }

    public ChessBoard(ChessBoard copyBoard) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                this.squares[i - 1][j - 1] = copyBoard.getPiece(new ChessPosition(i, j));
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Resets the board
        for (ChessPiece[] square : squares) {
            Arrays.fill(square, null);
        }

        ChessPiece.PieceType[] startingRowTypes = new ChessPiece.PieceType[8];
        startingRowTypes[0] = (ChessPiece.PieceType.ROOK);
        startingRowTypes[1] = (ChessPiece.PieceType.KNIGHT);
        startingRowTypes[2] = (ChessPiece.PieceType.BISHOP);
        startingRowTypes[3] = (ChessPiece.PieceType.QUEEN);
        startingRowTypes[4] = (ChessPiece.PieceType.KING);
        startingRowTypes[5] = (ChessPiece.PieceType.BISHOP);
        startingRowTypes[6] = (ChessPiece.PieceType.KNIGHT);
        startingRowTypes[7] = (ChessPiece.PieceType.ROOK);

        // Adds pieces to the board
        for (int i = 1; i <= 8; i++) {
            this.addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            this.addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));

            this.addPiece(new ChessPosition(8, i), new ChessPiece(ChessGame.TeamColor.BLACK, startingRowTypes[i - 1]));
            this.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, startingRowTypes[i - 1]));
        }
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }

    public void forceMove(ChessMove move) {
        if (move.getPromotionPiece() != null) {
            squares[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = new ChessPiece(squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1].getTeamColor(), move.promotionPiece);
        } else {
            squares[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1];
        }
        squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = null;
    }
}
