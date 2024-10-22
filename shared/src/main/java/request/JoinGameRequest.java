package request;

import chess.ChessGame;

public record JoinGameRequest(String playerColor, int gameID) {
}
