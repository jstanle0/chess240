package service;

import websocket.commands.UserGameCommand;

public class WebSocketService {
    public static void handleCommand(UserGameCommand command) {
        switch (command.getCommandType()) {
            case CONNECT -> handleConnection(command);
            case MAKE_MOVE -> handleMove(command);
            case LEAVE -> handleLeave(command);
            case RESIGN -> handleResign(command);
        }
    }

    private static void handleConnection(UserGameCommand command) {

    }

    private static void handleMove(UserGameCommand command) {

    }

    private static void handleLeave(UserGameCommand command) {

    }

    private static void handleResign(UserGameCommand command) {

    }
}
