package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board = new ChessBoard();
    TeamColor turn = TeamColor.WHITE;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (this.board.getPiece(startPosition) == null) {
            return null;
        }

        Collection<ChessMove> validMoves = this.board.getPiece(startPosition).pieceMoves(board, startPosition);

//        for (ChessMove move : validMoves) {
//
//        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves.contains(move)) {
            // Move piece
        } else {
            throw new InvalidMoveException("Not implemented");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);
        if (kingPos == null || teamColor != turn) {
            return false;
        }

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currentPos = new ChessPosition(i, j);
                ChessPiece currentPiece = board.getPiece(currentPos);
                ChessGame.TeamColor currentColor = currentPiece.getTeamColor();
                if (currentColor != teamColor) {
                    if (validMoves(currentPos).contains(kingPos)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (teamColor == turn) {
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    ChessPosition currentPos = new ChessPosition(i, j);
                    ChessPiece currentPiece = board.getPiece(currentPos);
                    ChessGame.TeamColor currentColor = currentPiece.getTeamColor();
                    if (currentColor != teamColor) {
                        if (validMoves(currentPos).contains(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        } else {
            return false;
        }
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece currentPiece = board.getPiece(new ChessPosition(i, j));
                ChessGame.TeamColor currentColor = currentPiece.getTeamColor();
                if (currentPiece.equals(ChessPiece.PieceType.KING) && currentColor == teamColor) {
                    return new ChessPosition(i, j);
                }
            }
        }

        return null;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
