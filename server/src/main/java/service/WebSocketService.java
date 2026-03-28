package service;

import chess.ChessGame;
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
        String connectingUser;
        ChessGame game;

        try {
            connectingUser = authDAO.getUsernameFromToken(UUID.fromString(command.getAuthToken()));
        } catch (DataAccessException | IllegalArgumentException e) {
            sendError(session, "you are not logged in.");
            return;
        }

        try {
            game = gameDAO.getGameObject(command.getGameID());
        } catch (DataAccessException e) {
            sendError(session, "invalid game id");
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

    }

    private static void handleLeave(UserGameCommand command, Session session) {
        String username;
        try {
            username = authDAO.getUsernameFromToken(UUID.fromString(command.getAuthToken()));
        } catch (DataAccessException e) {
            sendError(session, "Login is invalid.");
            return;
        }

        GameData gameData;
        try {
            gameData = gameDAO.getGame(command.getGameID());
        } catch (DataAccessException e) {
            sendError(session, "Invalid game id");
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

    }

    private static void sendNotifications(HashSet<Session> connections, ServerMessage message) {
        connections.forEach((s) -> {
            sendNotification(s, message);
        });
    }
    private static void sendNotification(Session s, ServerMessage message) {
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
