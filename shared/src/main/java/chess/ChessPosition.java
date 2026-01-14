package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;
    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]", this.getRow(), this.getColumn());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj.getClass() != ChessPosition.class) {
            return false;
        } else {
            ChessPosition cp = (ChessPosition) obj;
            return cp.getRow() == this.getRow() && cp.getColumn() == this.getColumn();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    public ChessPosition add(int rowAdd, int columnAdd) {
        return new ChessPosition(row + rowAdd, col + columnAdd);
    }
}
