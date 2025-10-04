package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessGame.TeamColor teamTurn;
    private ChessBoard gameBoard;

    public ChessGame() {

        teamTurn = TeamColor.WHITE;

        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
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
        ChessPiece piece = gameBoard.getPiece(startPosition);
        Collection<ChessMove> moveList = piece.pieceMoves(gameBoard, startPosition);
        return moveList;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //get piece, start, and end position
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = gameBoard.getPiece(startPosition);

        //make sure the move is valid
        //if move is valid
        //do moves
        //if it aint
        //tell the user invalid move
        Collection<ChessMove> moves = validMoves(startPosition);

        //check if the move is valid
        boolean canMove = false;
        for (ChessMove validMove : moves) {
            if (validMove.equals(move)) {
                canMove = true;
                break;
            }
        }

        if (canMove) {
            gameBoard.addPiece(endPosition, piece);

//            ChessPosition
//            piece.ChessPosition = new ChessPosition(endPosition)
//            board.
            //set a new piece where a new piece should go

            //i've got a piece here are my coords
            //the piece's coords are now set to somethign else

            //remove the original piece
        } else {
            throw new InvalidMoveException();
        }


        //make the move
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //go through all opponent's pieces
        //if one of the valid moves for any of them is the black king's position then you're in check
        ChessPosition kingPos = new ChessPosition(1, 1);
        boolean isInCheck = false;
        List<ChessMove> enemyMoves = new ArrayList<>();
        for (int startingRow = 1; startingRow <= 8; startingRow++) {
            for (int startingCol = 1; startingCol <= 8; startingCol++) {
                ChessPosition currPos = new ChessPosition(startingRow, startingCol);

                if (gameBoard.getPiece(currPos) == null) {
                    continue;
                }

                ChessPiece currPiece = gameBoard.getPiece(currPos);
                if (currPiece.getTeamColor() == teamColor) {
                    if (currPiece.getPieceType() == ChessPiece.PieceType.KING) {
                        kingPos = currPos;
                    }
                    continue;
                }

                enemyMoves.addAll(validMoves(currPos));
            }
        }
        for (ChessMove validMove : enemyMoves) {
            ChessPosition endPos = validMove.getEndPosition();
            if (endPos.equals(kingPos)) {
                isInCheck = true;
            }
        }
        //go through all enemy moves, if any are kingPos return true, else return false
        return isInCheck;

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
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }
}
