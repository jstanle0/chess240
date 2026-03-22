package ui;

import models.CreateGameBody;
import models.GamesListResponse;
import models.LoginUserData;
import models.UserData;

import java.util.Scanner;

/**
 * Contains extra helper functions for IOManager.
 * Has the functions that get specific objects for the client.
 */
abstract public class IOHelpers {
    protected Scanner scanner;
    protected String[] cachedCommand;

    public UserData getRegisterData() {
        if (cachedCommand != null && cachedCommand.length == 4) {
            return new UserData(cachedCommand[1], cachedCommand[2], cachedCommand[3]);
        }

        System.out.print("Enter a username: ");
        var username = scanner.nextLine();
        System.out.print("Enter a password: ");
        var password = scanner.nextLine();
        System.out.print("Enter your email: ");
        var email = scanner.nextLine();

        return new UserData(username, password, email);
    }

    public LoginUserData getLoginData() {
        if (cachedCommand != null && cachedCommand.length == 3) {
            return new LoginUserData(cachedCommand[1], cachedCommand[2]);
        }

        System.out.println("Enter your username: ");
        var username = scanner.nextLine();
        System.out.println("Enter your password: ");
        var password = scanner.nextLine();

        return new LoginUserData(username, password);
    }

    public CreateGameBody getCreateGameData() {
        if (cachedCommand != null && cachedCommand.length == 2) {
            return new CreateGameBody(cachedCommand[1]);
        }

        System.out.println("Enter the name of the game: ");
        return new CreateGameBody(scanner.nextLine());
    }

    public void printGameList(GamesListResponse gameList) {
        if (gameList.games().isEmpty()) {
            System.out.println("There are not any active games right now.");
        }
        for (var game : gameList.games()) {
            System.out.println("Game #" + game.gameID() + ": " + game.gameName());
            System.out.println("White player: " + (game.whiteUsername() != null ? game.whiteUsername() : "OPEN"));
            System.out.println("Black player: " + (game.blackUsername() != null ? game.blackUsername() : "OPEN"));
            System.out.println();
        }
    }
}
