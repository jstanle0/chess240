package chess;

import chess.moves.KingMoves;

import java.util.Collection;

public abstract class GameHelpers {
    protected ChessBoard board;
    protected ChessGame.TeamColor turn;

    /**
     * Makes a move on a given ChessBoard object
     * @param move ChessMove object
     * @param board ChessBoard object
     * @param revertMove will revert a given move after making it. Can be used to check if a hypothetical move is valid
     * @throws InvalidMoveException on an invalid move (not a move the piece can make/ends with own team in check)
     */
    protected void makeMoveOnBoard(ChessMove move, ChessBoard board, boolean revertMove) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        ChessPiece pieceToAdd = (move.getPromotionPiece() == null) ? piece : new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        if (!piece.pieceMoves(board, move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException();
        }
        board.addPiece(move.getStartPosition(), null);
        ChessPiece takenPiece = board.getPiece(move.getEndPosition()); // The move may be reverted
        board.addPiece(move.getEndPosition(), pieceToAdd);
        ChessPiece specialPiece = performSpecialHandling(move, board, piece, takenPiece); // To be able to revert a special piece
        if (checkCheck(piece.getTeamColor(), board, board.getKingPos(piece.getTeamColor()))) {
            board.addPiece(move.getEndPosition(), takenPiece);
            board.addPiece(move.getStartPosition(), piece);
            throw new InvalidMoveException();
        }
        if (revertMove) {
            board.addPiece(move.getEndPosition(), takenPiece);
            board.addPiece(move.getStartPosition(), piece);
            revertSpecialMove(move, board, specialPiece);
            return;
        }
        updateSpecialStatus(move, piece, board);
        turn = (turn == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
    }

    /**
     * Overload for checkCheck that ensures that no boards are mutated inadvertently.
     */
    protected boolean checkCheck(ChessGame.TeamColor teamColor, ChessBoard board, ChessPosition kingPos) {
        return checkCheck(teamColor, board, kingPos, false);
    }

    /**
     * Private function that checks if a chess board has a check state. Can be used to check hypothetical boards to ensure no checks are present.
     * Is realMove is true, the function resets any pawns to ensure that En Passant cannot be performed more than one turn after.
     */
    protected boolean checkCheck(ChessGame.TeamColor teamColor, ChessBoard board, ChessPosition kingPos, boolean realMove) {
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) {
                    continue;
                } else if (piece.getTeamColor() == teamColor) {
                    if (realMove && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                        piece.setSpecial(false);
                    }
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

    /**
     * Checks if a move would result in checking its own team
     */
    protected boolean checkHypotheticalCheck(ChessBoard board, ChessMove move) {
        try {
            makeMoveOnBoard(move, board, true); // If this error, the move is invalid
            return false;
        } catch (InvalidMoveException e) {
            return true;
        }
    }

    /**
     * Checks if a given move is En Passant based on the piece and taken piece, used in the makeMove function.
     */
    protected boolean checkIfEnPassant(ChessPiece piece, ChessPiece takenPiece) {
        return piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN && takenPiece == null;
    }

    /**
     * Checks if a given move is a castle, based on the piece and move
     */
    protected boolean checkIfCastle(ChessPiece piece, ChessMove move) {
        return piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && move.horizontalLength() > 1;
    }

    /**
     * Additional logic needed for performing a special move (En Passant/Castling)
     * @param move ChessMove object
     * @param board ChessBoard object
     * @param piece Piece performing the move
     * @param takenPiece Piece that was in the ending location of the move (nullable)
     * @return ChessPiece representing the piece affected by the special move (rook in castle/other pawn in en passant)
     * @throws InvalidMoveException If an invalid castle is attempted
     */
    protected ChessPiece performSpecialHandling(ChessMove move, ChessBoard board, ChessPiece piece, ChessPiece takenPiece) throws InvalidMoveException {
        if (checkIfEnPassant(piece, takenPiece)) {
            ChessPosition enPassantTaken = new ChessPosition(
                    move.getStartPosition().getRow(),
                    move.getEndPosition().getColumn()
            );
            ChessPiece specialPiece = board.getPiece(enPassantTaken);
            board.addPiece(enPassantTaken, null);
            return specialPiece;
        }
        if (checkIfCastle(piece, move)) {
            if (checkInvalidCastle(board, move, piece)) {
                board.addPiece(move.getEndPosition(), takenPiece);
                board.addPiece(move.getStartPosition(), piece);
                throw new InvalidMoveException();
            }
            boolean positiveDirection = move.getEndPosition().getColumn() > move.getStartPosition().getColumn();
            ChessPosition rookPos = KingMoves.getDefaultRookFromKing(
                    move.getStartPosition(),
                    positiveDirection
            );
            ChessPiece rook = board.getPiece(rookPos);
            board.addPiece(rookPos, null);
            board.addPiece(move.getEndPosition().add(0, positiveDirection ? -1 : 1), rook);
            return rook;
        }
        return null;
    }

    /**
     * Additional logic behind reverting a special move, if the move must be reverted for any reason
     * @param move ChessMove object
     * @param board ChessBoard object
     * @param takenPiece Pawn taken via En Passant or rook moved via castling
     */
    protected void revertSpecialMove(ChessMove move, ChessBoard board, ChessPiece takenPiece) {
        if (takenPiece != null) {
            if (takenPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                ChessPosition enPassantTaken = new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn());
                board.addPiece(enPassantTaken, takenPiece);
            } else if (takenPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                boolean positiveDirection = move.getEndPosition().getColumn() > move.getStartPosition().getColumn();
                ChessPosition rookPos = KingMoves.getDefaultRookFromKing(
                        move.getStartPosition(),
                        positiveDirection
                );
                board.addPiece(rookPos, takenPiece);
                board.addPiece(move.getEndPosition().add(0, positiveDirection ? -1 : 1), null);
            }
        }
    }

    /**
     * Function used to change the special status on the chess pieces involved in a move. This is if a pawn can be taken
     * via en passant or if a rook/king has moved (removing the possibility of check)
     */
    protected void updateSpecialStatus(ChessMove move, ChessPiece piece, ChessBoard board) {
        if (checkCheck(piece.getTeamColor(), board, board.getKingPos(piece.getTeamColor()), true)) {
            board.getPiece(board.getKingPos(piece.getTeamColor())).setSpecial(false);
        }
        switch(piece.getPieceType()) {
            case KING, ROOK -> piece.setSpecial(false);
            case PAWN -> {
                if (move.verticalLength() > 1) {
                    piece.setSpecial(true);
                }
            }
        }
    }

    /**
     * Checks if a king would be in check at the end or middle of the castle.
     */
    protected boolean checkInvalidCastle(ChessBoard board, ChessMove move, ChessPiece king) {
        ChessMove middleMove = new ChessMove(
                move.getEndPosition(),
                move.getEndPosition().add(0, move.getEndPosition().getColumn() > move.getStartPosition().getColumn() ? -1 : 1),
                null
        );
        return checkCheck(king.getTeamColor(), board, move.getStartPosition()) || checkHypotheticalCheck(board, middleMove);
    }

    /**
     * Function that checks the surrounding squares to a given king position to see if the king has available moves
     */
    protected boolean checkSurroundings(ChessBoard board, ChessPosition kingPos) {
        ChessPiece king = board.getPiece(kingPos);
        Collection<ChessMove> possibleMoves = king.pieceMoves(board, kingPos);
        possibleMoves.removeIf(move -> checkHypotheticalCheck(board, move));
        board.addPiece(kingPos, king);
        return possibleMoves.isEmpty();
    }

    /**
     * Checks if a team has valid moves remaining on the board.
     * @param color Color of the team being evaluated
     * @param board ChessBoard object
     * @param kingChecked If the king is currently in check. If true, the function evaluates if a different piece can block the check.
     * @return If a team has no remaining valid moves
     */
    protected boolean teamOutOfMoves(ChessGame.TeamColor color, ChessBoard board, boolean kingChecked) {
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null || piece.getTeamColor() != color || piece.getPieceType() == ChessPiece.PieceType.KING) {
                    continue;
                }
                Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                if (!kingChecked && !moves.isEmpty()) {
                    return false;
                } else if (!kingChecked) {
                    continue;
                }
                for (ChessMove move : moves) {
                    if (!checkHypotheticalCheck(board, move)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
