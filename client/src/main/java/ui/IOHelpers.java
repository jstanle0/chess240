package ui;

import models.LoginUserData;
import models.UserData;

import java.util.Scanner;

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
}
