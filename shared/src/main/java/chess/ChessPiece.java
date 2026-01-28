package chess;

import chess.moves.*;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;
    private boolean special; // This is a boolean trigger for special moves. It behaves differently for different piece types.

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
        this.special = switch (type) {
            case ROOK, KING -> true; // Check if a piece has moved for castling
            default -> false;  // Stores if a pawn has a possibility to be en passant'd
        };
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
        Collection<ChessMove> output;
        switch (type) {
            case PAWN -> output = PawnMoves.getMoves(this, board, myPosition);
            case KNIGHT -> output = KnightMoves.getMoves(this, board, myPosition);
            case BISHOP -> output = BishopMoves.getMoves(this, board, myPosition);
            case ROOK -> output = RookMoves.getMoves(this, board, myPosition);
            case QUEEN -> output = QueenMoves.getMoves(this, board, myPosition);
            case KING -> output = KingMoves.getMoves(this, board, myPosition);
            default -> throw new RuntimeException("Invalid Piece");
        }

        return output;
    }

    public boolean getSpecial() { return special; }

    public void setSpecial(boolean s) { special = s; }

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
