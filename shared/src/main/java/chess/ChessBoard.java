package chess;

import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board;
    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow() - 1;
        int col = position.getColumn() - 1;
        if (this.verifyPosition(position)) {
            throw new RuntimeException("Invalid position");
        }

        board[row][col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = BoardStates.defaultState();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != ChessBoard.class) {
            return false;
        }
        ChessBoard otherCB = (ChessBoard) obj;
        for(int i = 0; i < this.board.length; i++) {
            for(int j = 0; j < this.board[i].length; j++) {
                if (!Objects.equals(otherCB.board[i][j], this.board[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * Verifies if a position exists within the bounds of the board. Returns false if the position is valid.
     */
    public boolean verifyPosition(ChessPosition position) {
        int row = position.getRow() - 1;
        int col = position.getColumn() - 1;
        return row > 7 || row < 0 || col > 7 || col < 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < this.board.length; i++) {
            sb.append("| ");
            for(int j = 0; j < this.board[i].length; j++) {
               sb.append(this.board[i][j]);
               sb.append(" | ");
            }
            if (i < this.board.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.toString());
    }
}
