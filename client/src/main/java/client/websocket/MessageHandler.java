package client.websocket;

import websocket.messages.*;

public interface MessageHandler {
    void errorMessageNotify(ErrorMessage errorMsg);

    void notificationMessageNotify(NotificationMessage notificationMsg);

    void loadGameMessageNotify(LoadGameMessage loadGameMsg);
}
