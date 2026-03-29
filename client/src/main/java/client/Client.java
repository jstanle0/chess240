package client;

import chess.ChessGame;
import http.ServerFacade;
import models.*;
import ui.IOManager;

import java.util.*;

public class Client {
    private final ServerFacade server;
    private UUID authToken;
    private final Scanner scanner = new Scanner(System.in);
    private final IOManager ioManager = new IOManager(scanner);
    private List<GameData> cachedGames;

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
            cachedGames = List.of(gameData);
            System.out.println("Game " + gameData.gameName() + " has been created. Join it as game #1.");
        } catch (ResponseException e) {
            ioManager.printResponseError(e, 13);
        }
    }

    private void handleListGames() {
        try {
            var gameListData = server.listGames(authToken.toString());
            cachedGames = gameListData.games().stream().sorted(Comparator.comparingInt(GameData::gameID)).toList();
            ioManager.printGameList(cachedGames);
        } catch (ResponseException e) {
            ioManager.printResponseError(e, 14);
        }
    }

    /**
     * Placeholder for websockets
     */
    private void handleJoinGame() {
        try {
            var colorAndNumber = ioManager.getJoinGameData();
            if (cachedGames == null || colorAndNumber.gameID() < 1 || colorAndNumber.gameID() > cachedGames.size()) {
                System.out.println("Game number is invalid. Please create a game or list games.");
                return;
            }
            var gameId = cachedGames.get(colorAndNumber.gameID() - 1).gameID();
            var body = new JoinGameBody(colorAndNumber.playerColor(), gameId);
            server.joinGame(body, authToken.toString());
            System.out.println("Successfully joined game.");
            printUpdatedGame(body.gameID());
            new GameClient(server, authToken, scanner, body.playerColor(), gameId, false).run();
        } catch (ResponseException e) {
            ioManager.printResponseError(e, 15);
        }
    }

    /**
     * Placeholder for websockets
     */
    private void handleObserveGame() {
        try {
            var number = ioManager.getObserveGameData();
            if (cachedGames == null || number < 1 || number > cachedGames.size()) {
                System.out.println("Game number is invalid. Please create a game or list games.");
                return;
            }
            var id = cachedGames.get(number - 1).gameID();
            printUpdatedGame(id);
            new GameClient(server, authToken, scanner, ChessGame.TeamColor.WHITE, id, true).run();
        } catch (ResponseException e) {
            ioManager.printResponseError(e, 15);
        }
    }

    private void printUpdatedGame(Integer gameId) throws ResponseException {
        var games = server.listGames(authToken.toString());
        cachedGames = games.games().stream().sorted(Comparator.comparingInt(GameData::gameID)).toList();
        for (int i = 0; i < cachedGames.size(); i++) {
            var game = cachedGames.get(i);
            if (Objects.equals(game.gameID(), gameId)) {
                ioManager.printGame(game, i + 1);
                return;
            }
        }
        throw new ResponseException("game not found", 404);
    }

}
