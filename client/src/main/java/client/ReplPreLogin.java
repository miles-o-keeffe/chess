package client;

import result.LoginResult;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ReplPreLogin {
    private final ClientPreLogin client;

    public ReplPreLogin(String serverUrl) {
        this.client = new ClientPreLogin(serverUrl);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_GREEN + "Welcome to 240 Chess. Type help to get started.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }

            if (client.getState() == ClientPreLogin.State.SIGNEDIN) {
                new ReplLoggedIn(client.getServerURL()).run();
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + "[LOGGED_OUT] " + SET_TEXT_COLOR_RED + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
