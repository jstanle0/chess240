package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.List;

public class RookMoves extends MoveCalculations {
    public static List<ChessMove> getMoves(ChessPiece rook, ChessBoard board, ChessPosition position) {
        return ChessMove.positionsToMoves(
                getStraight(7, MoveDirection.ALL, board, position, rook.getTeamColor()),
                position);
    }
}
