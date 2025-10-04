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
    private ChessBoard copyBoard;
    private boolean usingCopyBoard;

    public ChessGame() {

        teamTurn = TeamColor.WHITE;

        gameBoard = new ChessBoard();
        gameBoard.resetBoard();

        copyBoard = gameBoard.clone();
        usingCopyBoard = false;
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
        TeamColor color = piece.getTeamColor();

        Collection<ChessMove> allMoveList = piece.pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> finalMoveList = new ArrayList<>();
        for (ChessMove move : allMoveList) {
            copyBoard = gameBoard.clone();
            usingCopyBoard = true;

            //do the move NOT caring for anything
            if (move.getPromotionPiece() == null) {
                copyBoard.addPiece(move.getEndPosition(), piece);
            } else {
                copyBoard.addPiece(move.getEndPosition(), new ChessPiece(teamTurn, move.getPromotionPiece()));
            }
            copyBoard.addPiece(startPosition, null);

            //is king in check? if not add the move
            if (!isInCheck(color)) {
                finalMoveList.add(move);
            }
            usingCopyBoard = false;
        }
        return finalMoveList;
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
        if (gameBoard.getPiece(startPosition) == null) {
            throw new InvalidMoveException();
        }
        ChessPiece piece = gameBoard.getPiece(startPosition);

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }
        //get a list of all valid moves
        Collection<ChessMove> moves = validMoves(startPosition);

        //check if the move is valid
        boolean canMove = false;
        for (ChessMove validMove : moves) {
            if (validMove.equals(move)) {
                canMove = true;
                break;
            }
        }

        //if it can move then move it, if not throw error
        if (canMove) {
            if (move.getPromotionPiece() == null) {
                gameBoard.addPiece(endPosition, piece);
            } else {
                gameBoard.addPiece(endPosition, new ChessPiece(teamTurn, move.getPromotionPiece()));
            }
            gameBoard.addPiece(startPosition, null);

            //change teams
            if (teamTurn == TeamColor.WHITE) {
                teamTurn = TeamColor.BLACK;
            } else {
                teamTurn = TeamColor.WHITE;
            }

        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessBoard boardToUse;
        if (usingCopyBoard) {
            boardToUse = copyBoard;
        } else {
            boardToUse = gameBoard;
        }
        //go through all opponent's pieces
        //if one of the valid moves for any of them is the black king's position then you're in check
        ChessPosition kingPos = new ChessPosition(1, 1);
        boolean isInCheck = false;
        List<ChessMove> enemyMoves = new ArrayList<>();
        for (int startingRow = 1; startingRow <= 8; startingRow++) {
            for (int startingCol = 1; startingCol <= 8; startingCol++) {
                ChessPosition currPos = new ChessPosition(startingRow, startingCol);

                if (boardToUse.getPiece(currPos) == null) {
                    continue;
                }

                ChessPiece currPiece = boardToUse.getPiece(currPos);
                if (currPiece.getTeamColor() == teamColor) {
                    if (currPiece.getPieceType() == ChessPiece.PieceType.KING) {
                        kingPos = currPos;
                    }
                    continue;
                }

                //i shouldn't care about valid moves here- i just can't move into check
                enemyMoves.addAll(currPiece.pieceMoves(boardToUse, currPos));
            }
        }
        for (ChessMove validMove : enemyMoves) {
            ChessPosition endPos = validMove.getEndPosition();
            if (endPos.equals(kingPos)) {
                isInCheck = true;
                break;
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
        boolean isInCheckmate = false;

        //go through all our pieces
        //put all the moves in there
        if (isInCheck(teamColor)) {
            List<ChessMove> myPossibleMoves = new ArrayList<>();
            for (int startingRow = 1; startingRow <= 8; startingRow++) {
                for (int startingCol = 1; startingCol <= 8; startingCol++) {
                    ChessPosition currPos = new ChessPosition(startingRow, startingCol);

                    if (gameBoard.getPiece(currPos) == null) {
                        continue;
                    }

                    ChessPiece currPiece = gameBoard.getPiece(currPos);
                    if (currPiece.getTeamColor() == teamColor) {
                        myPossibleMoves.addAll(validMoves(currPos));
                    }
                }
            }
            if (myPossibleMoves.isEmpty()) {
                isInCheckmate = true;
            }
        }

        return isInCheckmate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        boolean isInStalemate = false;
        //go through all our pieces
        //put all the moves in there
        if (!isInCheck(teamColor)) {
            List<ChessMove> myPossibleMoves = new ArrayList<>();
            for (int startingRow = 1; startingRow <= 8; startingRow++) {
                for (int startingCol = 1; startingCol <= 8; startingCol++) {
                    ChessPosition currPos = new ChessPosition(startingRow, startingCol);

                    if (gameBoard.getPiece(currPos) == null) {
                        continue;
                    }

                    ChessPiece currPiece = gameBoard.getPiece(currPos);
                    if (currPiece.getTeamColor() == teamColor) {
                        myPossibleMoves.addAll(validMoves(currPos));
                    }
                }
            }
            if (myPossibleMoves.isEmpty()) {
                isInStalemate = true;
            }
        }

        return isInStalemate;
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
