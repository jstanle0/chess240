package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

/*
 * Helper class that provides the functions for calculating different types of moves.
 */
public class MoveCalculations {
    public enum MoveDirection {
        UP,
        DOWN,
        ALL
    }

    private static List<ChessPosition> followLine(int maxLength, int rowIter, int columnIter, ChessBoard board, ChessPosition currentPos, ChessGame.TeamColor color) {
        List<ChessPosition> output = new ArrayList<>();
        for (int i = 1; i <= maxLength; i++) {
            ChessPosition nextPos = currentPos.add(rowIter * i, columnIter * i);
            if (board.verifyPosition(nextPos)) {
                break;
            }
            ChessPiece piece = board.getPiece(nextPos);
            if (piece != null) {
                if (piece.getTeamColor() == color) {
                    break;
                }
                output.add(nextPos);
                break;
            }
            output.add(nextPos);
        }

        return output;
    }

    public static List<ChessPosition> getDiagonals(int maxLength, MoveDirection direction, ChessBoard board, ChessPosition currentPos, ChessGame.TeamColor color) {
        //Instantiate booleans for each direction. They determine if a given direction is valid to move in.
        boolean up = direction == MoveDirection.ALL || direction == MoveDirection.UP;
        boolean down = direction == MoveDirection.ALL || direction == MoveDirection.DOWN;

        List<ChessPosition> output = new ArrayList<>();
        if (up) {
            output.addAll(followLine(maxLength, 1, 1, board, currentPos, color));
            output.addAll(followLine(maxLength, 1, -1, board, currentPos, color));
        }
        if (down) {
            output.addAll(followLine(maxLength, -1, 1, board, currentPos, color));
            output.addAll(followLine(maxLength, -1, -1, board, currentPos, color));
        }

        return output;
    }

    public static List<ChessPosition> getStraight(int maxLength, MoveDirection direction, ChessBoard board, ChessPosition currentPos, ChessGame.TeamColor color) {
        boolean up = direction == MoveDirection.ALL || direction == MoveDirection.UP;
        boolean down = direction == MoveDirection.ALL || direction == MoveDirection.DOWN;

        List<ChessPosition> output = new ArrayList<>();
        if (up) {
            output.addAll(followLine(maxLength, 1, 0, board, currentPos, color));
        }
        if (down) {
            output.addAll(followLine(maxLength, -1, 0, board, currentPos, color));
        }
        if (up && down) { //Both means that ALL is the selected move direction
            output.addAll(followLine(maxLength, 0, 1, board, currentPos, color));
            output.addAll(followLine(maxLength, 0, -1, board, currentPos, color));
        }

        return output;
    }

    public static List<ChessPosition> getLs(int maxLength, ChessBoard board, ChessPosition currentPos, ChessGame.TeamColor color) {
        List<ChessPosition> output = new ArrayList<>();

        //For simplicity, this is a list of possible displacements a knight can make. Then each one is iterated over.
        int[][] possibleMoves = {
                {-1, 2},
                {-1, -2},
                {1, 2},
                {1, -2},
                {-2, 1},
                {-2, -1},
                {2, 1},
                {2, -1}
        };
        for (int[] move : possibleMoves) {
            output.addAll(followLine(maxLength, move[0], move[1], board, currentPos, color));
        }
        return output;
    }
}
