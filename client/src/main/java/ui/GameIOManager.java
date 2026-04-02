package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import models.ResponseException;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameIOManager {
    private static final Map<String, Integer> COLUMN_LETTERS = Map.of(
            "a", 1,
            "b", 2,
            "c", 3,
            "d", 4,
            "e", 5,
            "f", 6,
            "g", 7,
            "h", 8
    );

    private final Scanner scanner;
    private String[] cachedCommand;

    public GameIOManager(Scanner s) {
        scanner = s;
    }

    public void printHelp(boolean isObserver) {
        System.out.print("""
                    Instructions:
                    "1": Display help information
                    "2": Leave game
                    "3": Redraw chess board
                    "4": Highlight legal moves
                    """);
        if (isObserver) {
            System.out.println();
            return;
        }
        System.out.println("""
                    "5": Make move
                    "6": Resign game
                    """);
    }

    public Integer getCommandCode() {
        cachedCommand = null;
        var command = scanner.nextLine().split("\\s+");
        if (command.length > 1) {
            cachedCommand = command;
        }
        return switch (command[0].toLowerCase()) {
            case "2", "leave", "quit" -> 2;
            case "3", "redraw" -> 3;
            case "4", "highlight" -> 4;
            case "5", "move" -> 5;
            case "6", "resign" -> 6;
            default -> 1;
        };
    }

    public void printMessage(ServerMessage message) {
        System.out.println(SET_TEXT_COLOR_GREEN + message.getMessage() + RESET_TEXT_COLOR);
    }

    public void printError(ServerMessage message) {
        System.out.println(SET_TEXT_COLOR_RED + message.getErrorMessage() + RESET_TEXT_COLOR);
    }

    public void printGameMessage(ServerMessage message, ChessGame.TeamColor team) {
        printGame(message.getGame(), team, null);
    }

    public void printGame(ChessGame game, ChessGame.TeamColor team, ChessPosition highlightedPiece) {
        Collection<ChessMove> highlightedMoves = null;
        if (highlightedPiece != null) {
            var selectedPiece = game.getBoard().getPiece(highlightedPiece);
            if (selectedPiece != null) {
                highlightedMoves = game.validMoves(highlightedPiece);
            }
        }
        GamePrinter.printBoard(game.getBoard(), team, highlightedMoves);
    }

    public void verifyResign() throws ResponseException {
        System.out.print("Are you sure you want to resign? (y/n)");
        var confirmation = scanner.nextLine();
        if (!confirmation.equalsIgnoreCase("y")) {
            throw new ResponseException("Resignation cancelled.", 400);
        }
    }

    public ChessPosition getPosition() throws ResponseException {
        if (cachedCommand != null && cachedCommand.length == 2) {
            return parsePosition(cachedCommand[1]);
        }
        System.out.print("Enter the piece (format \"a1\"): ");
        var stringPosition = scanner.nextLine();
        return parsePosition(stringPosition);
    }

    public ChessMove getMove() throws ResponseException {
        if (cachedCommand != null) {
            if (cachedCommand.length == 3) {
                return new ChessMove(parsePosition(cachedCommand[1]), parsePosition(cachedCommand[2]), null);
            } else if (cachedCommand.length == 4) {
                return new ChessMove(
                        parsePosition(cachedCommand[1]),
                        parsePosition(cachedCommand[2]),
                        ChessPiece.PieceType.valueOf(
                                cachedCommand[3].toUpperCase()
                        )
                );
            }
        }

        System.out.print("Enter the start position (format \"a1\"): ");
        var startPos = parsePosition(scanner.nextLine());
        System.out.print("Enter the end position (format \"a1\"): ");
        var endPos = parsePosition(scanner.nextLine());
        System.out.print("Enter piece to promote to (nothing if not applicable): ");
        ChessPiece.PieceType type;
        try {
            type = ChessPiece.PieceType.valueOf(scanner.nextLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            type = null;
        }
        return new ChessMove(startPos, endPos, type);
    }

    private ChessPosition parsePosition(String s) throws ResponseException {
        if (s.length() != 2) {
            throw new ResponseException("Invalid piece position format.", 400);
        }
        var r = s.charAt(1) - '0';
        if (r < 1 || r > 8) {
            throw new ResponseException("Row is invalid", 400);
        }

        var c = COLUMN_LETTERS.get(String.valueOf(s.charAt(0)).toLowerCase());
        if (c == null || c < 1 || c > 8) {
            throw new ResponseException("Column is invalid", 400);
        }

        return new ChessPosition(r, c);
    }
}
