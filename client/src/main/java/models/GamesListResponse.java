package models;

import java.util.Collection;

public record GamesListResponse(
        Collection<GameData> games
) {
}
