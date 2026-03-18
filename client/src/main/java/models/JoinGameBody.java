package models;

import chess.ChessGame;

public record JoinGameBody(
        ChessGame.TeamColor playerColor,
        Integer gameID
) {
}
