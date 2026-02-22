package service;

import dataaccess.AuthDAO;
import dataaccess.DAOs;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class DataService {
    static AuthDAO authDAO = DAOs.getAuthDAO();
    static GameDAO gameDAO = DAOs.getGameDAO();
    static UserDAO userDAO = DAOs.getUserDAO();

    public static void clearAllTables() {
        authDAO.clearTable();
        gameDAO.clearTable();
        userDAO.clearTable();
    }
}
