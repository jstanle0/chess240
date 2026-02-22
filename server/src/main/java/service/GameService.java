package service;

import dataaccess.DAOs;
import dataaccess.GameDAO;
import models.GameData;

public class GameService {
    static GameDAO gameDAO = DAOs.getGameDAO();

    public static GameData createGame(String gameName) {
        return gameDAO.createGame(gameName);
    }
}
