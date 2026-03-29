package ui;

import chess.*;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class GamePrinter {
    private static final Map<Integer, String> COLUMN_LETTERS = Map.of(
            1, "a",
            2, "b",
            3, "c",
            4, "d",
            5, "e",
            6, "f",
            7, "g",
            8, "h"
    );

    private static final Map<ChessPiece.PieceType, String> ABBREVIATED_PIECE_NAME = Map.of(
            ChessPiece.PieceType.PAWN, "P",
            ChessPiece.PieceType.ROOK, "R",
            ChessPiece.PieceType.KNIGHT, "N",
            ChessPiece.PieceType.BISHOP, "B",
            ChessPiece.PieceType.QUEEN, "Q",
            ChessPiece.PieceType.KING, "K"
    );


    public static void printBoard(ChessBoard board, ChessGame.TeamColor color, Collection<ChessMove> highlightedMoves) {
        //10 rows, 10 columns - 8 of the game, 2 labels on all sides
        if (color == ChessGame.TeamColor.WHITE) {
            for (int i = 9; i >= 0; i--) {
                System.out.print(SET_TEXT_BOLD);
                for (int j = 0; j < 10; j++) {
                    generateCell(board, highlightedMoves, i, j);
                }
                System.out.println(RESET_STYLING);
            }
        } else {
            for (int i = 0; i < 10; i++) {
                System.out.print(SET_TEXT_BOLD);
                for (int j = 9; j >= 0; j--) {
                    generateCell(board, highlightedMoves, i, j);
                }
                System.out.println(RESET_STYLING);
            }
        }
        System.out.println();
    }

    private static void generateCell(ChessBoard board, Collection<ChessMove> highlightedMoves, int i, int j) {
        boolean highlighted = false;
        if (highlightedMoves != null) {
            highlighted = highlightedMoves.stream().anyMatch((move) ->
                    Objects.equals(move.getEndPosition(), new ChessPosition(i, j)
                    ));
        }
        printCell(i, j, board, highlighted);
    }

    private static void printCell(Integer r, Integer c, ChessBoard board, boolean highlighted) {
        String output = addBackgroundColor("", r, c, highlighted);
        if ((r == 0 || r == 9) && (c == 0 || c == 9)) {
            output += "   ";
        } else if (r == 0 || r == 9) {
            output += " " + COLUMN_LETTERS.get(c) + " ";
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
                output += " " + ABBREVIATED_PIECE_NAME.get(piece.getPieceType()) + " ";
            }
        }

        System.out.print(output);
    }

    private static String addBackgroundColor(String output, Integer r, Integer c, boolean highlighted) {
        if (highlighted) {
            output += SET_BG_COLOR_GREEN;
            return output;
        }
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
