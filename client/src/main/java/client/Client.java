package client;

import http.ServerFacade;

public class Client {
    private final ServerFacade server;
    private String authToken;

    public Client(String url) { server = new ServerFacade(url); }
}
