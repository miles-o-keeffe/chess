package client;

import chess.ChessBoard;

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
        chessBoardDrawer.drawBoard(chessBoardToDraw, DrawChessBoard.BoardOrientation.BLACK);
        chessBoardDrawer.drawBreakBlack();
        chessBoardDrawer.drawBoard(chessBoardToDraw, DrawChessBoard.BoardOrientation.WHITE);
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
