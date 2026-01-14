package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.List;

public class KnightMoves {
    public static List<ChessMove> getMoves(ChessPiece bishop, ChessBoard board, ChessPosition position) {
        return ChessMove.positionsToMoves(
                MoveCalculations.getLs(1, board, position, bishop.getTeamColor()),
                position);
    }
}
