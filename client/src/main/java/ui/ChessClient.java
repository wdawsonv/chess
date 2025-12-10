package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.awt.Color.*;
import static ui.EscapeSequences.*;

public class ChessClient {

    private final ServerFacade facade;
    private State state = State.PRELOGIN;
    private String authToken = null;
    private List<GameData> recentGameList = new ArrayList<>();

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
                    case "creategame" -> createGame(params);
                    case "listgames" -> listGames();
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

            try {
                RegisterResult result = facade.addUser(user);
                this.state = State.POSTLOGIN;
                authToken = result.authToken();
                return "Successfully registered and logged in as " + result.username();
            } catch (Exception ex) {
                return "Username already taken";
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Please register with the format \"register [username] [password] [email]");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest request = new LoginRequest(username, password);


            try {
                LoginResult result = facade.login(request);
                this.state = State.POSTLOGIN;
                authToken = result.authToken();
                return "Successfully logged in as " + result.username();
            } catch (Exception ex) {
                return "Invalid username/password";
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Please login with the format \"login [username] [password]\"");
    }

    public String logout() throws ResponseException {
        facade.logout(authToken);

        this.state = State.PRELOGIN;

        return "Successfully logged you out";
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            String gameName = params[0];
            CreateRequest createRequest = new CreateRequest(gameName);

            try {
                CreateResult result = facade.createGame(createRequest, authToken);
                return "Game " + gameName + " successfully created";
            } catch (Exception ex) {
                return "That name is already taken, please choose a new one";
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Please create a game with the format \"creategame [name]\"");
    }

    public String listGames() throws ResponseException {

        try {
            recentGameList = facade.listGames(authToken);
            return recentGameList.toString();
        } catch (Exception ex) {
            return "Unauthenticated error, please exit the program and log in again";
        }
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
