package handler;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import service.WebSocketService;
import websocket.commands.UserGameCommand;

public class WebSocketHandler implements WsConnectHandler, WsCloseHandler, WsMessageHandler {
    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) {
        System.out.println("Websocket disconnected");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) {
        System.out.println("Websocket connected");
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws RuntimeException {
        try {
            UserGameCommand command = new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class);
            WebSocketService.handleCommand(command, wsMessageContext.session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
