package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.List;

public class RookMoves {
    public static List<ChessMove> getMoves(ChessPiece rook, ChessBoard board, ChessPosition position) {
        return ChessMove.positionsToMoves(
                MoveCalculations.getStraight(7, MoveCalculations.MoveDirection.ALL, board, position, rook.getTeamColor()),
                position);
    }
}
