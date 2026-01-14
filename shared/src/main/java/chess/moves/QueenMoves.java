package chess.moves;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class QueenMoves {
    public static List<ChessPosition> getMoves(ChessPiece king, ChessBoard board, ChessPosition position) {
        List<ChessPosition> output = new ArrayList<>();
        output.addAll(MoveCalculations.getDiagonals(7, MoveCalculations.MoveDirection.ALL, board, position, king.getTeamColor()));
        output.addAll(MoveCalculations.getStraight(7, MoveCalculations.MoveDirection.ALL, board, position, king.getTeamColor()));
        return output;
    }
}
