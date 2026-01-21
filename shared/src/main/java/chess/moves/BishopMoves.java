package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.List;

public class BishopMoves extends MoveCalculations {
    public static List<ChessMove> getMoves(ChessPiece bishop, ChessBoard board, ChessPosition position) {
        return ChessMove.positionsToMoves(
                getDiagonals(7, MoveDirection.ALL, board, position, bishop.getTeamColor()),
                position
        );
    }
}
