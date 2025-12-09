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

    private String visitorName = null;
    private final ServerFacade server;
    private State state = State.PRELOGIN;


    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
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
            return switch (cmd) {
                case "register" -> register(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            UserData user = new UserData(username,password, email);
            //serverFacade.adduser
            var result = new StringBuilder();
            var gson = new Gson();
            result.append(gson.toJson(server.addUser(user)));

            return result.toString();
        }
        throw new ResponseException(ResponseException.Code.ClientError, "register parameter length");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest request = new LoginRequest(username, password);

            var result = new StringBuilder();
            var gson = new Gson();
            result.append(gson.toJson(server.login(request)));

            return result.toString();
        }
        throw new ResponseException(ResponseException.Code.ClientError, "login failure");
    }

    public String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - to exit the program
                help - displays possible commands
                """;
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>>" + GREEN);
    }
}
