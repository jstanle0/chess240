package http;

import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void handleMessage(ServerMessage message);
}
