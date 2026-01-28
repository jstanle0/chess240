package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PawnMoves extends MoveCalculations {
    public static ChessPiece.PieceType[] possiblePromotions = {
            ChessPiece.PieceType.QUEEN,
            ChessPiece.PieceType.ROOK,
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.KNIGHT
    };

    public static List<ChessMove> getMoves(ChessPiece pawn, ChessBoard board, ChessPosition position) {
        List<ChessMove> output = new ArrayList<>();
        var moveDirection = (pawn.getTeamColor() == ChessGame.TeamColor.WHITE)
                ? MoveDirection.UP
                : MoveDirection.DOWN;

        List<ChessPosition> diagonalMoves = getDiagonals(1, moveDirection, board, position, pawn.getTeamColor());
        for (ChessPosition move : diagonalMoves) {
            var piece = board.getPiece(move);
            if (piece != null) { // No piece returns means that it isn't taking, therefore it's an invalid pawn move
                addWithPromotion(output, move, position);
            } else if (checkEnPassant(board, move, position)) {
                output.add(new ChessMove(position, move, null, ChessMove.SpecialMoves.ENPASSANT));
            }
        }

        int straightLength = 1;
        // Check for a double move. This can be determined from position since pawns moving the other direction hit the end of the board.
        if (position.getRow() == 2 || position.getRow() == 7) {
            straightLength = 2;
        }
        List<ChessPosition> straightMoves = getStraight(straightLength, moveDirection, board, position, pawn.getTeamColor());
        for (ChessPosition move : straightMoves) {
            var piece = board.getPiece(move);
            if (piece == null) { // If there's a piece, the pawn is trying to take illegally
                addWithPromotion(output, move, position);
            }
        }

        return output;
    }

    private static void addWithPromotion(List<ChessMove> out, ChessPosition move, ChessPosition position) {
        if (move.getRow() == 8 || move.getRow() == 1) { //Pawns can't get into the promotion zone on their own side
            for (ChessPiece.PieceType promotion : possiblePromotions) {
                out.add(new ChessMove(position, move, promotion));
            }
        } else {
            out.add(new ChessMove(position, move, null));
        }
    }

    private static boolean checkEnPassant(ChessBoard board, ChessPosition move, ChessPosition position) {
        ChessPosition enPassantPos = new ChessPosition(position.getRow(), move.getColumn());
        ChessPiece piece = board.getPiece(enPassantPos);
        return piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN && piece.getSpecial();
    }
}
