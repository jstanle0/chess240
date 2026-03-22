package ui;

import java.util.Map;

public class ResponseErrorMessages {
    public static final Map<Integer, String> READABLE_COMMAND_NAMES = Map.of(
            1, "get help",
            2, "exit",
            3, "login",
            4, "register account",
            12, "logout",
            13, "create game",
            14, "list games",
            15, "join game",
            16, "observe game"
    );
    public static final String SERVER_ERROR = "The server failed to complete the action. Please try again later.";
    public static final String UNAUTHORIZED_ERROR = "Your login has expired. Please log out and log in again.";
    public static final String INVALID_REQUEST_ERROR = "The information provided is invalid. Please ensure everything is formatted correctly.";
    public static final String USERNAME_TAKEN_ERROR = "This username is already taken. Please try a different username.";
    public static final String INCORRECT_PASSWORD_ERROR = "The password entered is incorrect.";
    public static final String DUPLICATE_LOGOUT_ERROR = "You have already logged out.";
    public static final String GAME_TAKEN_ERROR = "The requested slot in this game has already been taken. Please try in a different color or game.";

    public static String getErrorMessage(Integer commandCode, Integer exceptionCode) {
        var output = "Failed to " + READABLE_COMMAND_NAMES.get(commandCode) + ". ";

        output += switch (exceptionCode) {
            case 400 -> INVALID_REQUEST_ERROR;
            case 401 -> switch (commandCode) {
                case 3 -> INCORRECT_PASSWORD_ERROR;
                case 12 -> DUPLICATE_LOGOUT_ERROR;
                case null, default -> UNAUTHORIZED_ERROR;
            };
            case 403 -> switch (commandCode) {
                case 15 -> GAME_TAKEN_ERROR;
                case null, default -> USERNAME_TAKEN_ERROR;
            };
            case null, default -> SERVER_ERROR;
        };
        return output;
    }
}
