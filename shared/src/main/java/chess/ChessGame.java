package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() {
        turn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        moves.removeIf(move -> checkHypotheticalCheck(piece.getTeamColor(), board, move));
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != turn) {
            throw new InvalidMoveException();
        }
        makeMoveOnBoard(move, board, false);
    }

    private void makeMoveOnBoard(ChessMove move, ChessBoard board, boolean revertMove) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        ChessPiece pieceToAdd = (move.getPromotionPiece() == null) ? piece : new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        if (!piece.pieceMoves(board, move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException();
        }
        board.addPiece(move.getStartPosition(), null);
        ChessPiece takenPiece = board.getPiece(move.getEndPosition()); // The move may be reverted
        board.addPiece(move.getEndPosition(), pieceToAdd);
        if (isInCheck(piece.getTeamColor())) {
            board.addPiece(move.getEndPosition(), takenPiece);
            board.addPiece(move.getStartPosition(), piece);
            throw new InvalidMoveException();
        }
        if (revertMove) {
            board.addPiece(move.getEndPosition(), takenPiece);
            board.addPiece(move.getStartPosition(), piece);
            return;
        }
        turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     * Since check can be made by a discovered attack, all pieces must be evaluated for a check.
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return checkCheck(teamColor, board, board.getKingPos(teamColor));
    }

    /**
     * Private function that checks if a chess board has a check state. Can be used to check hypothetical boards to ensure no checks are present.
     */
    private boolean checkCheck(TeamColor teamColor, ChessBoard board, ChessPosition kingPos) {
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null || piece.getTeamColor() == teamColor) {
                    continue;
                }
                Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(kingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkHypotheticalCheck(TeamColor color, ChessBoard board, ChessMove move) {
        try {
            makeMoveOnBoard(move, board, true); // If this error, the move is invalid
            return false;
        } catch (InvalidMoveException e) {
            return true;
        }
    }

    /**
     * Function that checks the surrounding squares to a given king position to see if the king has available moves
     */
    private boolean checkSurroundings(TeamColor color, ChessBoard board, ChessPosition kingPos) {
        ChessPiece king = board.getPiece(kingPos);
        Collection<ChessMove> possibleMoves = king.pieceMoves(board, kingPos);
        //board.addPiece(kingPos, null); //Remove the king. Since the king can't block itself, this ensures all squares checked actually aren't threatened
        possibleMoves.removeIf(move -> checkHypotheticalCheck(color, board, move));
        board.addPiece(kingPos, king);
        return possibleMoves.isEmpty();
    }

    private boolean teamHasMoves(TeamColor color, ChessBoard board, boolean kingChecked) {
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null || piece.getTeamColor() != color || piece.getPieceType() == ChessPiece.PieceType.KING) {
                    continue;
                }
                Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                if (!kingChecked && !moves.isEmpty()) {
                    return true;
                } else if (kingChecked) {
                    for (ChessMove move : moves) {
                        if (!checkHypotheticalCheck(color, board, move)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return checkCheck(teamColor, board, board.getKingPos(teamColor))
                && checkSurroundings(teamColor, board, board.getKingPos(teamColor))
                && !teamHasMoves(teamColor, board, true);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !checkCheck(teamColor, board, board.getKingPos(teamColor)) && checkSurroundings(teamColor, board, board.getKingPos(teamColor)) && !teamHasMoves(teamColor, board, false);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ChessGame cg = (ChessGame) obj;
        return turn.equals(cg.getTeamTurn()) && board.equals(cg.getBoard());
    }
}
