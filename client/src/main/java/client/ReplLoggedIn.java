package client;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class ReplLoggedIn {
    private final ClientLoggedIn client;

    public ReplLoggedIn(String serverUrl, String currentAuthToken) {
        this.client = new ClientLoggedIn(serverUrl, currentAuthToken);
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.print(msg);
            }

            if (client.getGameJoinedID() > 0) {
                int newGameJoinedID = client.getGameJoinedID();
                client.setGameJoinedID(0);
                new ReplGamePlay(client.getServerURL(), client.getCurrentAuthToken(), newGameJoinedID).run();
            }
        }

        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_GREEN + "[LOGGED_IN] " + SET_TEXT_COLOR_RED + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
