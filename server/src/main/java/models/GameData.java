package models;

public record GameData(
        Integer gameId,
        String whiteUsername,
        String blackUsername,
        String gameName
) {
}
