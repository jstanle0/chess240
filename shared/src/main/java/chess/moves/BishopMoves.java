package chess.moves;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.List;

public class BishopMoves {
    public static List<ChessPosition> getMoves(ChessPiece bishop, ChessBoard board, ChessPosition position) {
        return MoveCalculations.getDiagonals(7, MoveCalculations.MoveDirection.ALL, board, position, bishop.getTeamColor());
    }
}
