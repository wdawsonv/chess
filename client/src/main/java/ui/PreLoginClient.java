package ui;

import com.google.gson.Gson;
import com.sun.nio.sctp.NotificationHandler;
import exception.ResponseException;
import model.LoginRequest;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;

public class PreLoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.PRELOGIN;

    public PreLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
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
}
