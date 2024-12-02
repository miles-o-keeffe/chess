package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board = new ChessBoard();
    TeamColor turn = TeamColor.WHITE;
    private boolean isGameOver = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(getBoard(), chessGame.getBoard()) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoard(), turn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", turn=" + turn +
                '}';
    }

    public ChessGame() {
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    private void changeTurns() {
        if (this.getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
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

        TeamColor currentTeamColor = this.board.getPiece(startPosition).getTeamColor();
        Collection<ChessMove> validMoves = this.board.getPiece(startPosition).pieceMoves(board, startPosition);

        validMoves.removeIf(move -> !checkMove(move, currentTeamColor));

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public ChessGame makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves == null) {
            throw new InvalidMoveException("Not implemented");
        } else if (validMoves.contains(move) && this.board.getPiece(move.startPosition).getTeamColor() == turn) {
            this.board.forceMove(move);
            this.changeTurns();
        } else {
            throw new InvalidMoveException("Not implemented");
        }
        return this;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);
        if (kingPos == null) {
            return false;
        }

        for (int i = 1; i < 9; i++) {
            if (inCheckLogic(teamColor, i, kingPos)) {
                return true;
            }
        }

        return false;
    }

    private boolean inCheckLogic(TeamColor teamColor, int i, ChessPosition kingPos) {
        for (int j = 1; j < 9; j++) {
            ChessPosition currentPos = new ChessPosition(i, j);
            ChessPiece currentPiece = board.getPiece(currentPos);
            if (currentPiece == null) {
                continue;
            }
            TeamColor currentColor = currentPiece.getTeamColor();
            if (currentColor != teamColor) {
                for (ChessMove move : currentPiece.pieceMoves(this.board, currentPos)) {
                    if (move.getEndPosition().equals(kingPos)) {
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
        if (teamColor == this.turn && isInCheck(teamColor)) {
            return !this.getTeamValidMoves(teamColor);
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (teamColor == this.turn && !isInCheck(teamColor)) {
            return !this.getTeamValidMoves(teamColor);
        }

        return false;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece currentPiece = board.getPiece(new ChessPosition(i, j));
                if (currentPiece == null) {
                    continue;
                }
                ChessGame.TeamColor currentColor = currentPiece.getTeamColor();
                if (currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentColor == teamColor) {
                    return new ChessPosition(i, j);
                }
            }
        }

        return null;
    }

    private boolean checkMove(ChessMove move, TeamColor teamColor) {
        ChessBoard originalBoard = new ChessBoard(this.board);
        this.board.forceMove(move);
        if (isInCheck(teamColor)) {
            this.board = new ChessBoard(originalBoard);
            return false;
        }

        this.board = new ChessBoard(originalBoard);
        return true;
    }

    /**
     * Returns true is teamColors pieces have valid moves
     * False otherwise
     */
    private boolean getTeamValidMoves(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currentPos = new ChessPosition(i, j);
                ChessPiece currentPiece = board.getPiece(currentPos);
                if (currentPiece == null) {
                    continue;
                }
                ChessGame.TeamColor currentColor = currentPiece.getTeamColor();
                if (currentColor == teamColor) {
                    if (!validMoves(currentPos).isEmpty()) {
                        return true;
                    }
                }
            }
        }

        return false;
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

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }
}
