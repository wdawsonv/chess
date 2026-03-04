package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard gameBoard;
    

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.gameBoard = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece myPiece = gameBoard.getPiece(startPosition);
        if (myPiece == null) {return null;}
        TeamColor myColor = myPiece.getTeamColor();

        Collection<ChessMove> allMoves = myPiece.pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> verifiedMoves = new ArrayList<>();

        for (ChessMove move : allMoves) {
            ChessPiece victimPiece = null;
            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();

            if (gameBoard.getPiece(endPos) != null) {
                victimPiece = gameBoard.getPiece(endPos);
            }

            gameBoard.addPiece(endPos, myPiece);
            if (!isInCheck(myColor)) {
                verifiedMoves.add(move);
            }

            gameBoard.addPiece(startPos, myPiece);
            gameBoard.addPiece(endPos, victimPiece);
        }

        return verifiedMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece.PieceType promPiece = move.getPromotionPiece();
        ChessPiece piece = gameBoard.getPiece(start);

        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        TeamColor moveColor = piece.getTeamColor();

        //throw arror if any of these happen:
        if (moveColor != getTeamTurn()) {
            throw new InvalidMoveException("It is not that team's turn");
        } //else if ()
            //your king ends up in check at the end of the turn
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition myKingPosition = findKingPosition(teamColor);
        boolean isChecked = false;

        for (int i=1; i<9; i++) {
            for (int j=1; j<9; j++) {
                ChessPosition testStartPosition = new ChessPosition(i, j);
                ChessPiece testPiece = gameBoard.getPiece(testStartPosition);

                if (testPiece == null) {continue;}
                if (testPiece.getTeamColor() == teamColor) {continue;}

                Collection<ChessMove> enemyPieceMoves = testPiece.pieceMoves(gameBoard, testStartPosition);

                for (ChessMove move : enemyPieceMoves) {
                    if (move.getEndPosition() == myKingPosition) {
                        isChecked = true;
                    }
                }


            }
        }

        return isChecked;
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int i=1; i<9; i++) {
            for (int j=1; j<9; j++) {
                ChessPosition possiblePosition = new ChessPosition(i, j);

                if (gameBoard.getPiece(possiblePosition) == null) {continue;}
                if (gameBoard.getPiece(possiblePosition).getPieceType() == ChessPiece.PieceType.KING &&
                        gameBoard.getPiece(possiblePosition).getTeamColor() == teamColor) {
                    return possiblePosition;
                }

            }
        }

        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }


}
