package ui;

import java.util.Scanner;

import static java.awt.Color.*;
import static ui.EscapeSequences.*;

public class Repl {
    private final PreLoginClient client;

    public Repl(String serverUrl) {
        client = new PreLoginClient(serverUrl);
        //how can I use State to swap between the clients?
    }

    public void run() {
        System.out.println("♕♕♕♕♕♕ Welcome to chessland!!!!!, sign in to start");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!"quit".equals(result)) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if (result == null) {
                    result = "";
                }
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                System.out.print(e.toString());
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>>" + GREEN);
    }
}
