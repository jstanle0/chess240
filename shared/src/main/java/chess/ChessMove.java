package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition start;
    private final ChessPosition end;
    private final ChessPiece.PieceType promotion;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.start = startPosition;
        this.end = endPosition;
        this.promotion = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotion;
    }

    @Override
    public String toString() {
        return (this.promotion == null)
                ? this.start.toString() + " -> " + this.end.toString() + "\n"
                : this.start.toString() + " -> " + this.end.toString() + "(" + this.promotion + ")\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != ChessMove.class) {
            return false;
        }
        ChessMove cm = (ChessMove) obj;
        return Objects.equals(cm.getStartPosition(), start) && Objects.equals(cm.getEndPosition(), end) && cm.getPromotionPiece() == promotion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promotion);
    }

    /**
     * Helper function to convert a move to a position. Doesn't support promotion pieces.
     */
    public static List<ChessMove> positionsToMoves(List<ChessPosition> positions, ChessPosition currentPosition) {
        List<ChessMove> output = new ArrayList<>();
        for (ChessPosition position : positions) {
            output.add(new ChessMove(currentPosition, position, null));
        }
        return output;
    }

    /**
     * Helper function to check if en passant is possible
     * @return vertical distance a pawn has traveled
     */
    public int verticalLength() {
        return Math.abs(start.getRow() - end.getRow());
    }

    public int horizontalLength() {
        return Math.abs(start.getColumn() - end.getColumn());
    }
}
