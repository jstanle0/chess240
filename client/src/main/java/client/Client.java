package client;

import http.ServerFacade;
import models.ExitException;
import ui.IOManager;

import java.util.Scanner;

public class Client {
    private final ServerFacade server;
    private String authToken;
    private final Scanner scanner = new Scanner(System.in);
    private final IOManager IOManager = new IOManager(scanner);

    public Client(String url) { server = new ServerFacade(url); }

    public void run() {
        IOManager.printIntroduction();
        IOManager.printHelp(false);
        while (true) {
            try {
                var code = IOManager.getCommandCode();
                handleCommand(code);
            } catch (ExitException e) {
                IOManager.printExit();
                break;
            } catch (Exception e) {
                IOManager.printError(e);
                break;
            }
        }
    }

    private void handleCommand(Integer code) throws ExitException {
        switch (code) {
            case 2 -> throw new ExitException();
            case null, default -> IOManager.printHelp(authToken != null);
        }
    }
}
