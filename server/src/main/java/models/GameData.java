package models;

public record GameData(
        Integer gameID,
        String whiteUsername,
        String blackUsername,
        String gameName
) {
}
