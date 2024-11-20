package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private String message;

    public NotificationMessage(ServerMessageType type, String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
