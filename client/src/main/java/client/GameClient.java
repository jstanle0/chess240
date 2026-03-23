package client;

import chess.ChessGame;
import http.ServerFacade;
import models.ExitException;
import ui.GameIOManager;

import java.util.Scanner;
import java.util.UUID;

/**
 * Separate client that handles the REPL loop for gameplay
 */
public class GameClient {
    private final ServerFacade server;
    private UUID authToken;
    private final GameIOManager ioManager;
    private final ChessGame game = new ChessGame();
    private final ChessGame.TeamColor team;

    public GameClient(ServerFacade s, UUID a, Scanner sc, ChessGame.TeamColor c) {
        server = s;
        authToken = a;
        ioManager = new GameIOManager(sc);
        team = c;
    }

    public void run() {
        ioManager.printGame(game, team);
        ioManager.printHelp();

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

    private void handleCommand(Integer code) throws ExitException {
        switch (code) {
            case 2 -> throw new ExitException();
            case 3 -> ioManager.printGame(game, team);
            default -> ioManager.printHelp();
        }
    }
}
