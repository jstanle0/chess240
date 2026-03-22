package client;

import http.ServerFacade;
import models.ExitException;
import models.LoginUserData;
import models.ResponseException;
import models.UserData;
import ui.IOManager;

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
            case null, default -> ioManager.printHelp(authToken != null);
        }
    }

    private void handleLogin() {
        LoginUserData data = ioManager.getLoginData();
        try {
            var response = server.login(data);
            authToken = response.authToken();
            System.out.println("Successfully logged in.");
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
}
