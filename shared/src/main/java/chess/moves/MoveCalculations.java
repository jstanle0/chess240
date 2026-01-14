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
    public static List<ChessPosition> followLine(int maxLength, int rowIter, int columnIter, ChessBoard board, ChessPosition currentPos, ChessGame.TeamColor color) {
        List<ChessPosition> output = new ArrayList<>();
        for (int i = 1; i <= maxLength; i++) {
            ChessPosition nextPos = currentPos.add(rowIter * i, columnIter * i);
            if (!board.verifyPosition(nextPos)) {
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
        //Instantiate booleans for each direction. They determine if a given direction is still valid in the loop.
        boolean upLeft, upRight;
        upLeft = upRight = direction == MoveDirection.ALL || direction == MoveDirection.UP;
        boolean downLeft, downRight;
        downLeft = downRight = direction == MoveDirection.ALL || direction == MoveDirection.DOWN;

        List<ChessPosition> output = new ArrayList<>();
        if (upLeft) {
            output.addAll(followLine(maxLength, -1, 1, board, currentPos, color));
        }
        if (upRight) {
            output.addAll(followLine(maxLength, 1, 1, board, currentPos, color));
        }
        if (downLeft) {
            output.addAll(followLine(maxLength, -1, -1, board, currentPos, color));
        }
        if (downRight) {
            output.addAll(followLine(maxLength, 1, -1, board, currentPos, color));
        }

        return output;
    }
}
