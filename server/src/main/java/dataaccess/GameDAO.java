package dataaccess;

public interface GameDAO {
    void clearGameDB();

    boolean getGame(String gameName);

    int createGame(String gameName);
}