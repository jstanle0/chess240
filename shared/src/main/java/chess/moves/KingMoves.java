package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class KingMoves extends MoveCalculations {
    public static List<ChessMove> getMoves(ChessPiece king, ChessBoard board, ChessPosition position) {
        List<ChessPosition> output = new ArrayList<>();
        output.addAll(getDiagonals(1, MoveDirection.ALL, board, position, king.getTeamColor()));
        output.addAll(getStraight(1, MoveDirection.ALL, board, position, king.getTeamColor()));
        return ChessMove.positionsToMoves(output, position);
    }
}
