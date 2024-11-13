package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import result.ListGameData;
import result.ListGamesResult;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class ClientGamePlay {
    private final String serverURL;
    private final ServerFacade serverFacade;
    private String currentAuthToken;
    private int gameID;
    private final DrawChessBoard chessBoardDrawer = new DrawChessBoard();

    public ClientGamePlay(String serverURL, String currentAuthToken, int gameID) {
        this.serverURL = serverURL;
        serverFacade = new ServerFacade(serverURL);
        this.currentAuthToken = currentAuthToken;
        this.gameID = gameID;
    }

    public void drawGame(ChessBoard chessBoardToDraw) {
        chessBoardDrawer.drawBoard(chessBoardToDraw, DrawChessBoard.boardOrientation.BLACK);
        chessBoardDrawer.drawBreakBlack();
        chessBoardDrawer.drawBoard(chessBoardToDraw, DrawChessBoard.boardOrientation.WHITE);
    }

    public String help() {
        return "create <NAME> - a game\n" +
                "list - games\n" +
                "join <ID> [WHITE|BLACK] - a game\n" +
                "observe <ID> - a game\n" +
                "logout - when you are done\n" +
                "quit - playing chess\n" +
                "help - with possible commands";
    }
}
