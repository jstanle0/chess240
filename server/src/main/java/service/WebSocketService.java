package service;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.*;
import models.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketService {
    private static final UserDAO userDAO = DAOs.getUserDAO();
    private static final AuthDAO authDAO = DAOs.getAuthDAO();
    private static final GameDAO gameDAO = DAOs.getGameDAO();
    private static final Gson gson = new Gson();

    private static final ConcurrentHashMap<Integer, HashSet<Session>> connectionMap = new ConcurrentHashMap<>();

    public static void handleCommand(UserGameCommand command, Session session) {
        switch (command.getCommandType()) {
            case CONNECT -> handleConnection(command, session);
            case MAKE_MOVE -> handleMove(command, session);
            case LEAVE -> handleLeave(command, session);
            case RESIGN -> handleResign(command, session);
        }
    }

    private static void handleConnection(UserGameCommand command, Session session) {
        var connectingUser = authenticateUser(command, session);
        if (connectingUser == null) {
            return;
        }

        var game = getGame(command, session);
        if (game == null) {
            return;
        }

        var loadGameMessage = new ServerMessage(
                ServerMessage.ServerMessageType.LOAD_GAME,
                game
            );
        sendNotification(session, loadGameMessage);

        var connectionList = connectionMap.get(command.getGameID());
        if (connectionList == null) {
            connectionMap.put(command.getGameID(), new HashSet<>(Set.of(session)));
            return;
        }

        var message = new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("%s joined the game.", connectingUser));
        sendNotifications(connectionList, message);

        connectionList.add(session);
    }

    private static void handleMove(UserGameCommand command, Session session) {
        var username = authenticateUser(command, session);
        if (username == null) {
            return;
        }

        var game = safeGetGame(command, session);
        if (game == null) {
            return;
        }

        var gameData = getGameData(command, session);
        if (gameData == null) {
            return;
        }

        var teamColor = getTeamColor(gameData, username, session);
        if (teamColor == null) {
            return;
        }

        if (game.getTeamTurn() != teamColor) {
            sendError(session, "It isn't your turn");
            return;
        }

        var pieceToMove = game.getBoard().getPiece(command.getMove().getStartPosition());
        if (pieceToMove.getTeamColor() != teamColor) {
            sendError(session, "You cannot move this piece.");
            return;
        }

        try {
            game.makeMove(command.getMove());
        } catch (InvalidMoveException e) {
            sendError(session, "This move is invalid.");
            return;
        }

        if (game.isInCheckmate((teamColor == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE )) {
            game.disableGame();
        }

        gameDAO.updateGameObject(command.getGameID(), game);

        var sessionList = connectionMap.get(command.getGameID());
        var message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        sendNotifications(sessionList, message);
        sendNotificationsToOthers(
                sessionList,
                new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " has made a move."),
                session
                );
        if (game.isDisabled()) {
            sendNotifications(sessionList, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " has won the game!"));
        }
    }

    private static void handleLeave(UserGameCommand command, Session session) {
        var username = authenticateUser(command, session);
        if (username == null) {
            return;
        }

        var gameData = getGameData(command, session);
        if (gameData == null) {
            return;
        }

        if (Objects.equals(gameData.whiteUsername(), username)) {
            gameDAO.updateGame(new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName()));
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            gameDAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName()));
        }
        var connectionList = connectionMap.get(command.getGameID());
        connectionList.remove(session);
        sendNotifications(connectionList, new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                "%s has left the game."
        ));
    }

    private static void handleResign(UserGameCommand command, Session session) {
        var username = authenticateUser(command, session);
        if (username == null) {
            return;
        }

        var gameData = getGameData(command, session);
        if (gameData == null) {
            return;
        }

        ChessGame game = safeGetGame(command, session);
        if (game == null) {
            return;
        }

        var teamColor = getTeamColor(gameData, username, session);
        if (teamColor == null) {
            return;
        }

        var otherUsername = (teamColor == ChessGame.TeamColor.WHITE) ? gameData.blackUsername() : gameData.whiteUsername();

        game.disableGame();
        gameDAO.updateGameObject(command.getGameID(), game);

        var sessionList = connectionMap.get(command.getGameID());
        sendNotifications(sessionList, new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                username + " has resigned. " + otherUsername + " has won the game!")
        );
    }

    private static ChessGame safeGetGame(UserGameCommand command, Session session) {
        var game = getGame(command, session);
        if (game == null) {
            return null;
        }

        if (game.isDisabled()) {
            sendError(session, "This game is no longer active");
            return null;
        }

        return game;
    }

    private static ChessGame.TeamColor getTeamColor(GameData gameData, String username, Session session) {
        if (Objects.equals(gameData.whiteUsername(), username)) {
            return ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            return ChessGame.TeamColor.BLACK;
        } else {
            sendError(session, "Observers are unable to move pieces.");
            return null;
        }
    }

    private static String authenticateUser(UserGameCommand command, Session session) {
        try {
            return authDAO.getUsernameFromToken(UUID.fromString(command.getAuthToken()));
        } catch (DataAccessException | IllegalArgumentException e) {
            sendError(session, "Login is invalid.");
            return null;
        }
    }

    private static ChessGame getGame(UserGameCommand command, Session session) {
        try {
            return gameDAO.getGameObject(command.getGameID());
        } catch (DataAccessException e) {
            sendError(session, "invalid game id");
            return null;
        }
    }

    private static GameData getGameData(UserGameCommand command, Session session) {
        try {
            return gameDAO.getGame(command.getGameID());
        } catch (DataAccessException e) {
            sendError(session, "Invalid game id");
            return null;
        }
    }

    private static void sendNotifications(HashSet<Session> connections, ServerMessage message) {
        connections.forEach((s) -> {
            sendNotification(s, message);
        });
    }
    private static void sendNotificationsToOthers(HashSet<Session> connections, ServerMessage message, Session currentSession) {
        connections.forEach((s) -> {
            if (s == currentSession) {
                return;
            }
            sendNotification(s, message);
        });
    }
    private static void sendNotification(Session s, ServerMessage message) {
        System.out.println("sending notification");
        if (s.isOpen()) {
            try {
                s.getRemote().sendString(gson.toJson(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendError(Session session, String messageString) {
        var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, messageString);
        if (session.isOpen()) {
            try {
                session.getRemote().sendString(gson.toJson(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
