package client;

import chess.ChessGame;
import http.NotificationHandler;
import http.ServerFacade;
import http.WebSocketFacade;
import models.ExitException;
import models.ResponseException;
import ui.GameIOManager;
import websocket.messages.ServerMessage;

import java.util.Scanner;
import java.util.UUID;

/**
 * Separate client that handles the REPL loop for gameplay
 */
public class GameClient {
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private UUID authToken;
    private final GameIOManager ioManager;
    private ChessGame game;
    private final Integer gameId;
    private final ChessGame.TeamColor team;
    private final boolean isObserver;

    public GameClient(ServerFacade s, UUID a, Scanner sc, ChessGame.TeamColor c, Integer id, boolean isObs) throws ResponseException {
        server = s;
        authToken = a;
        ioManager = new GameIOManager(sc);
        team = c;
        gameId = id;
        isObserver = isObs;
        var notificationHandler = new NotificationHandler() {
            @Override
            public void handleMessage(ServerMessage message) {
                processMessage(message);
            }
        };
        ws = new WebSocketFacade(server.getServerUrl(), notificationHandler);
        ws.connect(gameId, authToken.toString());
    }

    public void run() {
        ioManager.printHelp(isObserver);

        while (true) {
            try {
                var code = ioManager.getCommandCode();
                handleCommand(code);
            } catch (ExitException e) {
                System.out.println("Leaving game...");
                break;
            }
        }
    }

    private void processMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                game = message.getGame();
                ioManager.printGameMessage(message, team);
            }
            case NOTIFICATION -> ioManager.printMessage(message);
            case ERROR -> ioManager.printError(message);
        }
    }

    private void handleCommand(Integer code) throws ExitException {
        switch (code) {
            case 2 -> handleLeave();
            case 3 -> ioManager.printGame(game, team, null);
            case 4 -> handleHighlight();
            case 5 -> handleMakeMove();
            case 6 -> handleResignGame();
            default -> ioManager.printHelp(isObserver);
        }
    }

    private void handleHighlight() {
        try {
            var pos = ioManager.getPosition();
            ioManager.printGame(game, team, pos);
        } catch (ResponseException e) {
            ioManager.printError(new ServerMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()));
        }
    }

    private void handleLeave() throws ExitException {
        try {
            ws.leave(gameId, authToken.toString());
        } catch (ResponseException e) {
            ioManager.printError(new ServerMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()));
        }
        throw new ExitException();
    }

    private void handleMakeMove() {
        if (isObserver) {
            ioManager.printHelp(true);
            return;
        }
        try {
            var move = ioManager.getMove();
            ws.makeMove(gameId, authToken.toString(), move);
        } catch (ResponseException e) {
            ioManager.printError(new ServerMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()));
        }
    }

    private void handleResignGame() {
        if (isObserver) {
            ioManager.printHelp(true);
            return;
        }
        try {
            ioManager.verifyResign();
            ws.resign(gameId, authToken.toString());
        } catch (ResponseException e) {
            ioManager.printError(new ServerMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()));
        }
    }
}
