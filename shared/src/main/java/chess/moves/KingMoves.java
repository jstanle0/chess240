package chess.moves;

import chess.*;

import java.util.*;

public class KingMoves extends MoveCalculations {
    static final Map<ChessGame.TeamColor, Collection<ChessPosition>> ROOK_POS = Map.of(
            ChessGame.TeamColor.WHITE, List.of(new ChessPosition(1, 1), new ChessPosition(1, 8)),
            ChessGame.TeamColor.BLACK, List.of(new ChessPosition(8, 1), new ChessPosition(8, 8))
    );

    public static List<ChessMove> getMoves(ChessPiece king, ChessBoard board, ChessPosition position) {
        List<ChessPosition> output = new ArrayList<>();
        output.addAll(getDiagonals(1, MoveDirection.ALL, board, position, king.getTeamColor()));
        output.addAll(getStraight(1, MoveDirection.ALL, board, position, king.getTeamColor()));
        if (king.getSpecial()) {
            output.addAll(getCastle(king, board, position));
        }
        return ChessMove.positionsToMoves(output, position);
    }

    public static ChessPosition getDefaultRookFromKing(ChessPosition kingPos, boolean greaterColumn) {
        return new ChessPosition(kingPos.getRow(), greaterColumn ? 8 : 1);
    }

    private static List<ChessPosition> getCastle(ChessPiece king, ChessBoard board, ChessPosition position) {
        List<ChessPosition> out = new ArrayList<>();
        for (ChessPosition pos : ROOK_POS.get(king.getTeamColor())) {
            ChessPiece rook = board.getPiece(pos);
            if (rook != null && rook.getPieceType() == ChessPiece.PieceType.ROOK && rook.getSpecial()) {
                if (pos.getColumn() > position.getColumn()) {
                    if (checkClear(board, position.getRow(), position.getColumn(), pos.getColumn())) {
                        out.add(position.add(0, 2));
                    }
                } else {
                    if (checkClear(board, position.getRow(), pos.getColumn(), position.getColumn())) {
                        out.add(position.add(0, -2));
                    }
                }
            }
        }
        return out;
    }

    private static boolean checkClear(ChessBoard board, int row, int startCol, int endCol) {
        for (int i = startCol + 1; i < endCol; i++) {
            if (board.getPiece(new ChessPosition(row, i)) != null) {
                return false;
            }
        }
        return true;
    }
}
