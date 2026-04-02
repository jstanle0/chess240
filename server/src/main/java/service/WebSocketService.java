package service;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
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
    private static final AuthDAO AUTH_DAO = DAOs.getAuthDAO();
    private static final GameDAO GAME_DAO = DAOs.getGameDAO();
    private static final Gson GSON = new Gson();

    private static final ConcurrentHashMap<Integer, HashSet<Session>> CONNECTION_MAP = new ConcurrentHashMap<>();

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

        var gameData = getGameData(command, session);
        String joinRole;
        if (gameData != null) {
            var color = getTeamColor(gameData, connectingUser);
            joinRole = switch (color) {
                case BLACK -> "black";
                case WHITE -> "white";
                case null -> "an observer";
            };
        } else {
            joinRole = "an observer";
        }

        var loadGameMessage = new ServerMessage(
                ServerMessage.ServerMessageType.LOAD_GAME,
                game
            );
        sendNotification(session, loadGameMessage);

        if (game.isDisabled()) {
            sendNotification(session, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "This game has ended."));
        }

        var connectionList = CONNECTION_MAP.get(command.getGameID());
        if (connectionList == null) {
            CONNECTION_MAP.put(command.getGameID(), new HashSet<>(Set.of(session)));
            return;
        }

        var message = new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("%s joined the game as %s.", connectingUser, joinRole));
        sendNotifications(connectionList, message);

        connectionList.add(session);
    }

    private enum GameNotificationState {
        CHECKMATE,
        STALEMATE,
        CHECK
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

        var teamColor = getTeamColor(gameData, username);
        if (teamColor == null) {
            sendError(session, "Observers are unable to move pieces.");
            return;
        }

        if (game.getTeamTurn() != teamColor) {
            sendError(session, "It isn't your turn");
            return;
        }

        var pieceToMove = game.getBoard().getPiece(command.getMove().getStartPosition());
        if (pieceToMove == null) {
            sendError(session, "There isn't a piece at this position.");
            return;
        }
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

        GameNotificationState notificationState = null;
        var otherTeamColor = (teamColor == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        var otherUsername = (otherTeamColor == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();

        if (game.isInCheckmate(otherTeamColor)) {
            game.disableGame();
            notificationState = GameNotificationState.CHECKMATE;
        } else if (game.isInStalemate(otherTeamColor)) {
            game.disableGame();
            notificationState = GameNotificationState.STALEMATE;
        } else if (game.isInCheck(otherTeamColor)) {
            notificationState = GameNotificationState.CHECK;
        }

        GAME_DAO.updateGameObject(command.getGameID(), game);

        var sessionList = CONNECTION_MAP.get(command.getGameID());
        var message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        sendNotifications(sessionList, message);
        sendNotificationsToOthers(
                sessionList,
                new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " has made a move."),
                session
                );
        if (notificationState != null) {
            var notification = switch (notificationState) {
                case CHECKMATE -> new ServerMessage(
                            ServerMessage.ServerMessageType.NOTIFICATION,
                            otherUsername + " is in checkmate. " + username + " has won the game!");
                case STALEMATE -> new ServerMessage(
                            ServerMessage.ServerMessageType.NOTIFICATION,
                            "The game has ended in a stalemate.");
                case CHECK -> new ServerMessage(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        otherUsername + " is in check."
                );
            };
            sendNotifications(sessionList, notification);
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
            GAME_DAO.updateGame(new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName()));
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            GAME_DAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName()));
        }
        var connectionList = CONNECTION_MAP.get(command.getGameID());
        connectionList.remove(session);
        sendNotifications(connectionList, new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("%s has left the game.", username)
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

        var teamColor = getTeamColor(gameData, username);
        if (teamColor == null) {
            sendError(session, "Observers are unable to resign.");
            return;
        }

        var otherUsername = (teamColor == ChessGame.TeamColor.WHITE) ? gameData.blackUsername() : gameData.whiteUsername();

        game.disableGame();
        GAME_DAO.updateGameObject(command.getGameID(), game);

        var sessionList = CONNECTION_MAP.get(command.getGameID());
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

    private static ChessGame.TeamColor getTeamColor(GameData gameData, String username) {
        if (Objects.equals(gameData.whiteUsername(), username)) {
            return ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }

    private static String authenticateUser(UserGameCommand command, Session session) {
        try {
            return AUTH_DAO.getUsernameFromToken(UUID.fromString(command.getAuthToken()));
        } catch (DataAccessException | IllegalArgumentException e) {
            sendError(session, "Login is invalid.");
            return null;
        }
    }

    private static ChessGame getGame(UserGameCommand command, Session session) {
        try {
            return GAME_DAO.getGameObject(command.getGameID());
        } catch (DataAccessException e) {
            sendError(session, "invalid game id");
            return null;
        }
    }

    private static GameData getGameData(UserGameCommand command, Session session) {
        try {
            return GAME_DAO.getGame(command.getGameID());
        } catch (DataAccessException e) {
            sendError(session, "Invalid game id");
            return null;
        }
    }

    private static void sendNotifications(HashSet<Session> connections, ServerMessage message) {
        connections.forEach((s) -> sendNotification(s, message));
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
        if (s.isOpen()) {
            try {
                s.getRemote().sendString(GSON.toJson(message));
            } catch (IOException e) {
                System.out.println("Failed to send notification: " + e.getMessage());
            }
        }
    }

    private static void sendError(Session session, String messageString) {
        var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, messageString);
        if (session.isOpen()) {
            try {
                session.getRemote().sendString(GSON.toJson(message));
            } catch (IOException e) {
                System.out.println("Failed to send error: " + e.getMessage());
            }
        }
    }
}
