package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    private final GameData gameData;

    public LoadGameMessage(ServerMessageType type, GameData gameData) {
        super(type);
        this.gameData = gameData;
    }

    public GameData getGame() {
        return gameData;
    }
}
