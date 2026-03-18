package models;

import java.util.UUID;

public record AuthData(
        String username,
        UUID authToken
) { }
