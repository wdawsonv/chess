package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import model.LoginRequest;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static java.awt.Color.*;
import static ui.EscapeSequences.*;

public class ChessClient {

    private final ServerFacade facade;
    private State state = State.PRELOGIN;
    private String authToken = null;

    public ChessClient(String serverUrl) {
        facade = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("♕♕♕♕♕♕ Welcome to chessland!!!!!, sign in to start");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!"quit".equals(result)) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
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

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state == State.PRELOGIN) {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            } else /*if (state == State.POSTLOGIN)*/ {
                return switch (cmd) {
                    case "logout" -> logout();
                    default -> help();
                };
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            UserData user = new UserData(username, password, email);
            var result = new StringBuilder();
            var gson = new Gson();
            var output = facade.addUser(user);
            result.append(gson.toJson(output));

            this.state = State.POSTLOGIN;

            return result.toString();
        }
        throw new ResponseException(ResponseException.Code.ClientError, "wrong register parameter length");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest request = new LoginRequest(username, password);

            var result = new StringBuilder();
            var gson = new Gson();
            result.append(gson.toJson(facade.login(request)));

            this.state = State.POSTLOGIN;

            return result.toString();
        }
        throw new ResponseException(ResponseException.Code.ClientError, "login failure");
    }

    public String logout() throws ResponseException {
        facade.logout();

        this.state = State.PRELOGIN;

        return "Successfully logged you out";
    }

    public String help() {
        if (state == State.PRELOGIN) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - to exit the program
                    help - displays possible commands
                    """;
        } else if (state == State.POSTLOGIN) {
            return """
                    logout - to leave
                    creategame - to make a new game
                    listgames - to see all running games
                    playgame - to join a game
                    observegame - to watch a game without playing
                    help - displays possible commands
                    """;
        } else {
            return "wah where am I";
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>>" + GREEN);
    }
}
