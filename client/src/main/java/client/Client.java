package client;

import http.ServerFacade;
import models.*;
import ui.IOManager;

import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

public class Client {
    private final ServerFacade server;
    private UUID authToken;
    private final Scanner scanner = new Scanner(System.in);
    private final IOManager ioManager = new IOManager(scanner);

    public Client(String url) { server = new ServerFacade(url); }

    public void run() {
        ioManager.printIntroduction();
        ioManager.printHelp(false);
        while (true) {
            try {
                var code = ioManager.getCommandCode(authToken != null);
                handleCommand(code);
            } catch (ExitException e) {
                ioManager.printExit();
                break;
            } catch (Exception e) {
                ioManager.printError(e);
                break;
            }
        }
    }

    private void handleCommand(Integer code) throws ExitException {
        switch (code) {
            case 2 -> throw new ExitException();
            case 3 -> handleLogin();
            case 4 -> handleRegister();
            case 12 -> handleLogout();
            case 13 -> handleCreateGame();
            case 14 -> handleListGames();
            case 15 -> handleJoinGame();
            case 16 -> handleObserveGame();
            case null, default -> ioManager.printHelp(authToken != null);
        }
    }

    private void handleLogin() {
        LoginUserData data = ioManager.getLoginData();
        try {
            var response = server.login(data);
            authToken = response.authToken();
            System.out.println("Successfully logged in.");
            ioManager.printHelp(true);
        } catch (ResponseException e) {
            ioManager.printResponseError(e, 3);
        }
    }

    private void handleRegister() {
        UserData data = ioManager.getRegisterData();
        try {
            var response = server.createAccount(data);
            authToken = response.authToken();
            System.out.println("Successfully created account.");
            ioManager.printHelp(true);
        } catch (ResponseException e) {
            ioManager.printResponseError(e, 4);
        }
    }

    private void handleLogout() {
        try {
            server.logout(authToken.toString());
            authToken = null;
            System.out.println("Successfully logged out.");
        } catch (ResponseException e) {
            ioManager.printResponseError(e, 12);
        }
    }

    private void handleCreateGame() {
        CreateGameBody body = ioManager.getCreateGameData();
        try {
            var gameData = server.createGame(body, authToken.toString());
            System.out.println("Game " + gameData.gameName() + " has been created. The game id is " + gameData.gameID() + ".");
        } catch (ResponseException e) {
            ioManager.printResponseError(e, 13);
        }
    }

    private void handleListGames() {
        try {
            var gameListData = server.listGames(authToken.toString());
            ioManager.printGameList(gameListData);
        } catch (ResponseException e) {
            ioManager.printResponseError(e, 14);
        }
    }

    private void handleJoinGame() {
        try {
            var body = ioManager.getJoinGameData();
            server.joinGame(body, authToken.toString());
            System.out.println("Successfully joined game.");
            printUpdatedGame(body.gameID());
        } catch (ResponseException e) {
            ioManager.printResponseError(e, 15);
        }
    }

    /**
     * Placeholder for websockets
     */
    private void handleObserveGame() {
        try {
            var id = ioManager.getObserveGameData();
            printUpdatedGame(id);
        } catch (ResponseException e) {
            ioManager.printResponseError(e, 15);
        }
    }

    private void printUpdatedGame(Integer gameId) throws ResponseException {
        var games = server.listGames(authToken.toString());
        var updatedGame = games.games().stream().filter(game -> Objects.equals(game.gameID(), gameId)).findFirst();
        if (updatedGame.isEmpty()) {
            throw new ResponseException("game not found", 404);
        }
        ioManager.printGame(updatedGame.get());
    }

}
