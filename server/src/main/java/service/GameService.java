package service;

import chess.ChessGame;
import dataaccess.DAOs;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import models.GameData;
import models.JoinGameBody;

import java.util.Objects;

public class GameService {
    static GameDAO gameDAO = DAOs.getGameDAO();

    public static GameData createGame(String gameName) {
        return gameDAO.createGame(gameName);
    }

    public static void joinGame(JoinGameBody data, String username) throws BadRequestResponse, ForbiddenResponse {
        GameData game;
        try {
            game = gameDAO.getGame(data.gameID());
        } catch (DataAccessException e) {
            throw new BadRequestResponse(e.getMessage());
        }
        String whiteUsername;
        String blackUsername;
        if (data.playerColor() == ChessGame.TeamColor.WHITE) {
            if (!Objects.equals(game.whiteUsername(), "")) {
                throw new ForbiddenResponse("team already taken");
            }
            whiteUsername = username;
            blackUsername = game.blackUsername();
        } else {
            if (!Objects.equals(game.blackUsername(), "")) {
                throw new ForbiddenResponse("team already taken");
            }
            blackUsername = username;
            whiteUsername = game.whiteUsername();
        }

        gameDAO.updateGame(new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName()));
    }
}
