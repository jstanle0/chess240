package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public class PawnMoves {
    public static ChessPiece.PieceType[] possiblePromotions = {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT};

    public static List<ChessMove> getMoves(ChessPiece pawn, ChessBoard board, ChessPosition position) {
        List<ChessMove> output = new ArrayList<>();
        var moveDirection = (pawn.getTeamColor() == ChessGame.TeamColor.WHITE) ? MoveCalculations.MoveDirection.UP : MoveCalculations.MoveDirection.DOWN;
        List<ChessPosition> diagonalMoves = MoveCalculations.getDiagonals(1, moveDirection, board, position, pawn.getTeamColor());
        for (ChessPosition move : diagonalMoves) {
            var piece = board.getPiece(move);
            if (piece != null) { // No piece returns means that it isn't taking, therefore it's an invalid pawn move
                if (move.getRow() == 8 || move.getRow() == 1) { //Pawns can't get into the promotion zone on their own side
                    for (ChessPiece.PieceType promotion : possiblePromotions) {
                        output.add(new ChessMove(position, move, promotion));
                    }
                } else {
                    output.add(new ChessMove(position, move, null));
                }
            }
        }
        int straightLength = 1;
        if ((pawn.getTeamColor() == ChessGame.TeamColor.WHITE && position.getRow() == 2) || (pawn.getTeamColor() == ChessGame.TeamColor.BLACK && position.getRow() == 7)) {
            straightLength = 2;
        }
        List<ChessPosition> straightMoves = MoveCalculations.getStraight(straightLength, moveDirection, board, position, pawn.getTeamColor());
        for (ChessPosition move : straightMoves) {
            var piece = board.getPiece(move);
            if (piece == null) { // If there's a piece, the pawn is trying to take illegally
                if (move.getRow() == 8 || move.getRow() == 1) { //Pawns can't get into the promotion zone on their own side
                    for (ChessPiece.PieceType promotion : possiblePromotions) {
                        output.add(new ChessMove(position, move, promotion));
                    }
                } else {
                    output.add(new ChessMove(position, move, null));
                }
            }
        }

        return output;
    }
}
