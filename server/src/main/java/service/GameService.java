package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DAOs;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import models.GameData;
import models.GamesListResponse;
import models.JoinGameBody;

import java.util.UUID;

public class GameService {
    static GameDAO gameDAO = DAOs.getGameDAO();
    static AuthDAO authDAO = DAOs.getAuthDAO();

    public static GamesListResponse getGames() {
        return gameDAO.getGames();
    }

    public static GameData createGame(String gameName) {
        return gameDAO.createGame(gameName);
    }

    public static void joinGame(JoinGameBody data, String tokenString) throws BadRequestResponse, ForbiddenResponse, UnauthorizedResponse {
        String username;
        try {
            username = authDAO.getUsernameFromToken(UUID.fromString(tokenString));
        } catch (DataAccessException e) {
            throw new UnauthorizedResponse("invalid token"); //This should already be checked earlier in the flow, this is a redundancy
        }
        GameData game;
        try {
            game = gameDAO.getGame(data.gameID());
        } catch (DataAccessException e) {
            throw new BadRequestResponse(e.getMessage());
        }
        String whiteUsername;
        String blackUsername;
        if (data.playerColor() == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) {
                throw new ForbiddenResponse("team already taken");
            }
            whiteUsername = username;
            blackUsername = game.blackUsername();
        } else {
            if (game.blackUsername() != null) {
                throw new ForbiddenResponse("team already taken");
            }
            blackUsername = username;
            whiteUsername = game.whiteUsername();
        }

        gameDAO.updateGame(new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName()));
    }
}
