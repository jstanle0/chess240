package chess;

public class BoardStates {
    public static ChessPiece[][] defaultState() {
        var board = new ChessPiece[8][8];

        //Place pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

        ChessPiece.PieceType[] placementOrder = {ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING};
        for (int i = 0; i < 5; i++) {
            board[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE, placementOrder[i]);
            board[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, placementOrder[i]);
            if (i < 3) {
                board[0][7-i] = new ChessPiece(ChessGame.TeamColor.WHITE, placementOrder[i]);
                board[7][7 - i] = new ChessPiece(ChessGame.TeamColor.BLACK, placementOrder[i]);
            }
        }

        return board;
    }
}
