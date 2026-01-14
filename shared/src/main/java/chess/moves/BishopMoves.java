package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.List;

public class BishopMoves {
    public static List<ChessMove> getMoves(ChessPiece bishop, ChessBoard board, ChessPosition position) {
        return ChessMove.positionsToMoves(
                MoveCalculations.getDiagonals(7, MoveCalculations.MoveDirection.ALL, board, position, bishop.getTeamColor()),
                position
        );
    }
}
