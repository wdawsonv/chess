package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import server.ServerFacade;

import java.util.*;

import static java.awt.Color.*;
import static ui.EscapeSequences.*;

public class ChessClient {

    private final ServerFacade facade;
    private State state = State.PRELOGIN;
    private String authToken = null;
    private GameList recentGameList = new GameList();
    private Map<Integer, Integer> idMatcher = new HashMap<>();

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
                System.out.print(SET_TEXT_COLOR_BLUE + result);
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
                    case "playgame" -> joinGame(params);
                    case "observegame" -> observeGame(params);
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

    public String joinGame(String... params) throws  ResponseException {
        if (params.length == 2) {
            int placeholderId = Integer.parseInt(params[0]);
            String teamColor = params[1].toUpperCase();
            int realID;
            try {
                realID = idMatcher.get(placeholderId);
            } catch (Exception ex) {
                return "type listgames to see a list of available games to join";
            }
            JoinRequest joinRequest = new JoinRequest(teamColor, realID);

            try {
                JoinResult result = facade.joinGame(joinRequest, authToken);
                state = State.GAMEPLAY;
                if (teamColor.equals("WHITE")) {
                    return "Game " + params[0] + " successfully joined\n" +
                            displayGameWhite(getGameFromId(realID)); //then display the chungus board (white side if white black side if black)
                } else {
                    return "Game " + params[0] + " successfully joined\n" +
                            displayGameBlack(getGameFromId(realID)); //then display the chungus board (white side if white black side if black)
                }
            } catch (Exception ex) {
                return "Unable to join that game/color, please try a different one";
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Please join a game with the format \"joingame [game ID] [WHITE/BLACK]\"");
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length == 1) {
            int placeholderId = Integer.parseInt(params[0]);
            int realID;
            try {
                realID = idMatcher.get(placeholderId);
            } catch (Exception ex) {
                return "type listgames to see a list of available games to join";
            }

            try {
                state = State.GAMEPLAY;
                return "Game " + params[0] + " successfully joined\n" +
                        displayGameWhite(getGameFromId(realID));
            } catch (Exception ex) {
                return "Unable to join that game, please try a different one";
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Please observe a game with the format \"observegame [game ID]\"");
    }

    private ChessGame getGameFromId(int gameID) {
        for (GameData game : recentGameList) {
            if (game.gameID() == gameID) {
                return game.game();
            }
        }
        return null;
    }

    private String displayGameBlack(ChessGame game) {
        StringBuilder display = new StringBuilder();
        ChessBoard board = game.getBoard();
        ChessPiece[][] squares = board.squares;

        //10 by 10 grid, top down right to left
        for (int row = 9; row >= 0; row--) {
            for (int col = 0; col < 10; col++) {
                if (row == 0 || row == 9 || col == 0 || col == 9) {
                    display.append(SET_BG_COLOR_LIGHT_GREY);

                    //put all outside logic in here
                    if ((row == 9 && col > 0 && col < 9) || (row == 0 && col > 0 && col < 9) ) {
                        String colVal = switch (col) {
                            case 1 -> "H ";
                            case 2 -> "  G ";
                            case 3 -> "  F ";
                            case 4 -> "  E ";
                            case 5 -> " D ";
                            case 6 -> "  C ";
                            case 7 -> "  B ";
                            case 8 -> "  A";
                            default -> "";
                        };
                        display.append(colVal);
                    } else if ((col == 9 && row > 0 && row < 9) || (col == 0 && row > 0 && row < 9) ) {
                        String rowVal = switch (row) {
                            case 1 -> " 8 ";
                            case 2 -> " 7 ";
                            case 3 -> " 6 ";
                            case 4 -> " 5 ";
                            case 5 -> " 4 ";
                            case 6 -> " 3 ";
                            case 7 -> " 2 ";
                            case 8 -> " 1 ";
                            default -> "";
                        };
                        display.append(rowVal);
                    } else {
                        display.append(EMPTY);
                    }
                } else {
                    int shiftRow = row - 1;
                    int shiftCol = col - 1;

                    if ((shiftRow + shiftCol) % 2 == 1) {
                        display.append(SET_BG_COLOR_DARK_GREY);
                    } else {
                        display.append(SET_BG_COLOR_BLACK);
                    }


                    ChessPiece piece = squares[shiftRow][7 - shiftCol];

                    if (piece == null) {
                        display.append(EMPTY);
                    } else {
                        display.append(pieceSymbol(piece));
                    }
                }

                display.append(RESET_BG_COLOR);
            }
            display.append("\n");
        }
        return display.toString();
    }

    private String displayGameWhite(ChessGame game) {
        StringBuilder display = new StringBuilder();
        ChessBoard board = game.getBoard();
        ChessPiece[][] squares = board.squares;

        //10 by 10 grid, top down right to left
        for (int row = 9; row >= 0; row--) {
            for (int col = 0; col < 10; col++) {
                if (row == 0 || row == 9 || col == 0 || col == 9) {
                    display.append(SET_BG_COLOR_LIGHT_GREY);

                    //put all outside logic in here
                    if ((row == 9 && col > 0 && col < 9) || (row == 0 && col > 0 && col < 9) ) {
                        String colVal = switch (col) {
                            case 1 -> "A ";
                            case 2 -> "  B ";
                            case 3 -> "  C ";
                            case 4 -> "  D ";
                            case 5 -> " E ";
                            case 6 -> "  F ";
                            case 7 -> "  G ";
                            case 8 -> "  H";
                            default -> "";
                        };
                        display.append(colVal);
                    } else if ((col == 9 && row > 0 && row < 9) || (col == 0 && row > 0 && row < 9) ) {
                        String rowVal = switch (row) {
                            case 1 -> " 1 ";
                            case 2 -> " 2 ";
                            case 3 -> " 3 ";
                            case 4 -> " 4 ";
                            case 5 -> " 5 ";
                            case 6 -> " 6 ";
                            case 7 -> " 7 ";
                            case 8 -> " 8 ";
                            default -> "";
                        };
                        display.append(rowVal);
                    } else {
                        display.append(EMPTY);
                    }
                } else {
                    int shiftRow = row - 1;
                    int shiftCol = col - 1;

                    if ((shiftRow + shiftCol) % 2 == 1) {
                        display.append(SET_BG_COLOR_DARK_GREY);
                    } else {
                        display.append(SET_BG_COLOR_BLACK);
                    }


                    ChessPiece piece = squares[7 - shiftRow][shiftCol];

                    if (piece == null) {
                        display.append(EMPTY);
                    } else {
                        display.append(pieceSymbol(piece));
                    }
                }

                display.append(RESET_BG_COLOR);
            }
            display.append("\n");
        }
        return display.toString();
    }

    private String pieceSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KING : BLACK_KING;
            case QUEEN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_PAWN : BLACK_PAWN;
        };
    }


    public String listGames() throws ResponseException {

        try {
            recentGameList = facade.listGames(authToken);

            idMatcher.clear();
            int idNumPublic = 1;
            for (GameData game : recentGameList) {
                idMatcher.put(idNumPublic, game.gameID());
                idNumPublic++;
            }

            StringBuilder finalString = new StringBuilder();
            finalString.append("\n---------------------------------------------------------\n");
            finalString.append(String.format("| %-8s | %-10s | %-13s | %-13s |\n",
                    "Game ID", "Game Name", "White Player", "Black Player"));

            int num = 1;
            for (GameData game : recentGameList) {
                finalString.append(String.format("| %-8s | %-10s | %-13s | %-13s |\n",
                        num,
                        game.gameName(),
                        game.whiteUsername() != null ? game.whiteUsername() : "AVAILABLE",
                        game.blackUsername() != null ? game.blackUsername() : "AVAILABLE"));
                num++;
            }

            finalString.append("---------------------------------------------------------\n");

            return finalString.toString();
        } catch (Exception ex) {
            return "No games in database, create a new one to begin";
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
        System.out.print("\n" + RESET_TEXT_COLOR + SET_TEXT_BLINKING + ">>>" + RESET_TEXT_BLINKING);
    }
}
