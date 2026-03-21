package ui;

import java.util.Scanner;

import static ui.EscapeSequences.WHITE_KING;

public class IOManager {
    private static Scanner scanner;
    public IOManager(Scanner s) {
        scanner = s;
    }

    public void printIntroduction() {
        System.out.println("Welcome to chess! " + WHITE_KING);
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
                        1. TODO
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

    public int getCommandCode() {
        var line = scanner.nextLine();
        var command = line.split("\\s+");
        return switch (command[0]) {
            case "2", "q", "quit" -> 2;
            case "3", "login" -> 3;
            case "4", "register" -> 4;
            default -> 1;
        };
    }
}
