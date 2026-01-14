package chess.moves;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.List;

public class RookMoves {
    public static List<ChessPosition> getMoves(ChessPiece rook, ChessBoard board, ChessPosition position) {
        return MoveCalculations.getStraight(7, MoveCalculations.MoveDirection.ALL, board, position, rook.getTeamColor());
    }
}
