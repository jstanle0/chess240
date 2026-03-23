package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Map;

import static ui.EscapeSequences.*;

public class GamePrinter {
    private static final Map<Integer, String> columnLetters = Map.of(
            0, "",
            1, "a",
            2, "b",
            3, "c",
            4, "d",
            5, "e",
            6, "f",
            7, "g",
            8, "h",
            9, ""
    );

    private static final Map<ChessPiece.PieceType, String> abbreviatedPieceName = Map.of(
            ChessPiece.PieceType.PAWN, "P",
            ChessPiece.PieceType.ROOK, "R",
            ChessPiece.PieceType.KNIGHT, "N",
            ChessPiece.PieceType.BISHOP, "B",
            ChessPiece.PieceType.QUEEN, "Q",
            ChessPiece.PieceType.KING, "K"
    );


    public static void printBoard(ChessBoard board, ChessGame.TeamColor color) {
        //10 rows, 10 columns - 8 of the game, 2 labels on all sides
        if (color == ChessGame.TeamColor.WHITE) {
            for (int i = 9; i >= 0; i--) {
                System.out.print(SET_TEXT_BOLD);
                for (int j = 0; j < 10; j++) {
                    printCell(i, j, board);
                }
                System.out.println(RESET_STYLING);
            }
        } else {
            for (int i = 0; i < 10; i++) {
                System.out.print(SET_TEXT_BOLD);
                for (int j = 9; j >= 0; j--) {
                    printCell(i, j, board);
                }
                System.out.println(RESET_STYLING);
            }
        }
        System.out.println();
    }

    private static void printCell(Integer r, Integer c, ChessBoard board) {
        String output = addBackgroundColor("", r, c);
        if ((r == 0 || r == 9) && (c == 0 || c == 9)) {
            output += "   ";
        } else if (r == 0 || r == 9) {
            output += " " + columnLetters.get(c) + " ";
        } else if (c == 0 || c == 9) {
            output += " " + r + " ";
        } else {
            var piece = board.getPiece(new ChessPosition(r, c));
            if (piece == null) {
                output += "   ";
            } else {
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    output += SET_TEXT_COLOR_RED;
                } else {
                    output += SET_TEXT_COLOR_BLUE;
                }
                output += " " + abbreviatedPieceName.get(piece.getPieceType()) + " ";
            }
        }

        System.out.print(output);
    }

    private static String addBackgroundColor(String output, Integer r, Integer c) {
        if (r == 0 || r == 9 || c == 0 || c == 9) {
            output += SET_BG_COLOR_LIGHT_GREY;
            output += SET_TEXT_COLOR_BLACK;
        } else {
            if ((r + c) % 2 == 0) {
                output += SET_BG_COLOR_BLACK;
            } else {
                output += SET_BG_COLOR_WHITE;
            }
        }
        return output;
    }
}
