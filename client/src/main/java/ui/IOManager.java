package ui;

import models.ResponseException;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class IOManager extends IOHelpers {
    public static final String PROMPT = SET_TEXT_BLINKING + ">> " + RESET_TEXT_BLINKING;

    public IOManager(Scanner s) {
        scanner = s;
    }

    public void printIntroduction() {
        System.out.println("Welcome to chess! " + WHITE_KING + "\n");
    }

    public void printResponseError(ResponseException e, Integer commandCode) {
        System.out.println(ResponseErrorMessages.getErrorMessage(commandCode, e.getCode()));
    }
    public void printError(Exception e) {
        System.out.println("ERROR: " + e.getMessage());
    }

    public void printExit() {
        System.out.println("Thanks for using chess!");
    }

    public void printHelp(Boolean isLoggedIn) {
        if (isLoggedIn) {
            System.out.println("""
                    Instructions:
                    "1": Display help information
                    "2": Logout
                    "3": Create Game
                    "4": List Games
                    "5": Play Game
                    "6": Observe Game
                    """);
        } else {
            System.out.println("""
                    Instructions:
                    "1": Display help information
                    "2": Quit program
                    "3": Login to an existing account
                    "4": Register a new user
                    """);
        }
    }

    public int getCommandCode(Boolean isLoggedIn) {
        cachedCommand = null;
        System.out.print(PROMPT);
        var line = scanner.nextLine();
        var command = line.split("\\s+");
        if (command.length > 1) {
            cachedCommand = command;
        }
        if (isLoggedIn) {
            return switch (command[0].toLowerCase()) {
                case "2", "logout" -> 12;
                case "3", "create" -> 13;
                case "4", "list" -> 14;
                case "5", "play", "join" -> 15;
                case "6", "observe" -> 16;
                default -> 1;
            };
        }
        return switch (command[0].toLowerCase()) {
            case "2", "q", "quit" -> 2;
            case "3", "login" -> 3;
            case "4", "register" -> 4;
            default -> 1;
        };
    }


}
