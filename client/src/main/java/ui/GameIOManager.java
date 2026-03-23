package ui;

import chess.ChessGame;

import java.util.Scanner;

public class GameIOManager {
    private final Scanner scanner;
    private String[] cachedCommand;

    public GameIOManager(Scanner s) {
        scanner = s;
    }

    public void printHelp() {
        System.out.println("""
                    Instructions:
                    "1": Display help information
                    "2": Leave game
                    "3": Redraw chess board
                    "4": Make move
                    "5": Highlight legal moves
                    "6": Resign game
                    """);
    }

    public Integer getCommandCode() {
        cachedCommand = null;
        System.out.print(IOManager.PROMPT);
        var command = scanner.nextLine().split("\\s+");
        if (command.length > 1) {
            cachedCommand = command;
        }
        return switch (command[0].toLowerCase()) {
            case "2", "leave", "quit" -> 2;
            case "3", "redraw" -> 3;
            case "4", "move" -> 4;
            case "5", "highlight" -> 5;
            default -> 1;
        };
    }

    public void printGame(ChessGame game, ChessGame.TeamColor team) {
        GamePrinter.printBoard(game.getBoard(), team);
    }
}
