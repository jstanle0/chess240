package chess;

import chess.moves.*;

import java.util.*;

import static java.util.Collections.emptyList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> output = new ArrayList<>();
        List<ChessPosition> validPositions = emptyList();
        switch (type) {
            case PAWN -> output = PawnMoves.getMoves(this, board, myPosition); //Pawn returns differently because it can promote
            case KNIGHT -> validPositions = KnightMoves.getMoves(this, board, myPosition);
            case BISHOP -> validPositions = BishopMoves.getMoves(this, board, myPosition);
            case ROOK -> validPositions = RookMoves.getMoves(this, board, myPosition);
            case QUEEN -> validPositions = QueenMoves.getMoves(this, board, myPosition);
            case KING -> validPositions = KingMoves.getMoves(this, board, myPosition);
            default -> throw new RuntimeException("Invalid Piece");
        }
        for (ChessPosition position : validPositions) {
            output.add(new ChessMove(myPosition, position, null));
        }

        return output;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != ChessPiece.class) {
            return false;
        }
        ChessPiece cp = (ChessPiece) obj;
        return cp.color == this.color && cp.type == this.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.color, this.type);
    }

    @Override
    public String toString() {
        return this.color + " " + this.type;
    }
}
