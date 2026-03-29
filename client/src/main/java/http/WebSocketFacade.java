package http;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import models.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler handler;
    Gson gson;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.handler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.gson = new Gson();
            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = gson.fromJson(message, ServerMessage.class);
                    notificationHandler.handleMessage(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ex.getMessage(), 500);
        }
    }

    public void connect(Integer gameId, String authToken) throws ResponseException {
        var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId);
        sendCommand(command);
    }

    public void leave(Integer gameId, String authToken) throws ResponseException {
        var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameId);
        sendCommand(command);
    }

    public void makeMove(Integer gameId, String authToken, ChessMove move) throws ResponseException {
        var command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameId, move);
        sendCommand(command);
    }

    public void resign(Integer gameId, String authToken) throws ResponseException {
        var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameId);
        sendCommand(command);
    }

    private void sendCommand(UserGameCommand command) throws ResponseException {
        try {
            session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException e) {
            throw new ResponseException(e, 500);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
