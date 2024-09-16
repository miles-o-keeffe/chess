package chess;

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
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int checkRow;
        int checkCol;
        ChessGame.TeamColor teamColor = board.getPiece(myPosition).getTeamColor();
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        switch(this.type) {
            case PieceType.KING:
                break;
            case PieceType.QUEEN:
                break;
            case PieceType.BISHOP:
                int[][] movementArray = new int[][]{{1, -1}, {-1, 1}, {-1, -1}, {1, 1}};
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
