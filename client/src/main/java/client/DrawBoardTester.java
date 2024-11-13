package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import static ui.EscapeSequences.*;

public class DrawBoardTester {
    public static void main(String[] args) {
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        DrawChessBoard drawChessBoard = new DrawChessBoard();
        drawChessBoard.drawBoard(chessBoard, DrawChessBoard.boardOrientation.BLACK);
        drawChessBoard.drawBreakBlack();
        drawChessBoard.drawBoard(chessBoard, DrawChessBoard.boardOrientation.WHITE);


    }
}
