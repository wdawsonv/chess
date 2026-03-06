package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
            gameBoard.addPiece(startPos, null);
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
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece.PieceType promPiece = move.getPromotionPiece();
        ChessPiece myPiece = gameBoard.getPiece(startPos);

        if (myPiece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        TeamColor moveColor = myPiece.getTeamColor();

        //throw error if any of these happen:
        if (moveColor != getTeamTurn()) {
            throw new InvalidMoveException("It is not that team's turn");
        }

        Collection<ChessMove> availableMoves = validMoves(startPos);
        if (!availableMoves.contains(move)) {
            throw new InvalidMoveException("Invalid Move");

        } else {

            if (teamTurn == TeamColor.BLACK) {
                setTeamTurn(TeamColor.WHITE);
            } else {
                setTeamTurn(TeamColor.BLACK);
            }
            gameBoard.addPiece(startPos, null);

            if (promPiece == null) {
                gameBoard.addPiece(endPos, myPiece);
            } else {
                gameBoard.addPiece(endPos, new ChessPiece(moveColor, promPiece));
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition myKingPosition = findKingPosition(teamColor);
        return isUnderAttack(teamColor, myKingPosition);
    }

    private boolean isUnderAttack(TeamColor teamColor, ChessPosition position) {
        boolean attacked = false;

        for (int i=1; i<9; i++) {
            for (int j=1; j<9; j++) {
                ChessPosition potentialEnemyPosition = new ChessPosition(i, j);
                ChessPiece potentialEnemyPiece = gameBoard.getPiece(potentialEnemyPosition);

                if (potentialEnemyPiece == null) {continue;}
                if (potentialEnemyPiece.getTeamColor() == teamColor) {continue;}

                Collection<ChessMove> enemyPieceMoves = potentialEnemyPiece.pieceMoves(gameBoard, potentialEnemyPosition);

                for (ChessMove move : enemyPieceMoves) {
                    if (move.getEndPosition().equals(position)) {
                        attacked = true;
                        break;
                    }
                }

            }
        }

        return attacked;
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
        boolean checkMated = false;
        if (!canBlockCheck(teamColor) && isInCheck(teamColor)) {
            checkMated = true;
        }

        return checkMated;
    }

    private boolean canBlockCheck(TeamColor teamColor) {
        //try all possible moves
        //in each move check if the king is under attack
        //if not then return true
        //otherwise return false
        boolean blockable = false;
        for (int i=1; i<9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition potentialTeammatePosition = new ChessPosition(i, j);
                ChessPiece potentialTeammatePiece = gameBoard.getPiece(potentialTeammatePosition);

                if (potentialTeammatePiece == null) {continue;}
                if (potentialTeammatePiece.getTeamColor() != teamColor) {continue;}

                Collection<ChessMove> teamPieceMoves = potentialTeammatePiece.pieceMoves(gameBoard, potentialTeammatePosition);

                for (ChessMove move : teamPieceMoves) {
                    //set some things in case we gotta undo
                    ChessPosition originalPosition = move.getStartPosition();
                    ChessPosition endPosition = move.getEndPosition();
                    ChessPiece movedPiece = gameBoard.getPiece(originalPosition);
                    boolean pieceIsTaken = false;
                    ChessPiece takenPiece = new ChessPiece(teamColor, ChessPiece.PieceType.PAWN); //TEMPORARY, WILL NEVER BE USED

                    if (gameBoard.getPiece(endPosition) != null) {
                        takenPiece = gameBoard.getPiece(endPosition);
                        pieceIsTaken = true;
                    }

                    gameBoard.addPiece(originalPosition, null);
                    gameBoard.addPiece(endPosition, movedPiece);

                    if (!isInCheck(teamColor)) {
                        blockable = true;
                    }

                    //now put everything back
                    gameBoard.addPiece(originalPosition, movedPiece);
                    if (pieceIsTaken) {
                        gameBoard.addPiece(endPosition, takenPiece);
                    } else {
                        gameBoard.addPiece(endPosition, null);
                    }

                }
            }
        }

        return blockable;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {

                ChessPosition potentialTeammatePosition = new ChessPosition(i, j);
                ChessPiece potentialTeammatePiece = gameBoard.getPiece(potentialTeammatePosition);

                if (potentialTeammatePiece == null) {continue;}
                if (potentialTeammatePiece.getTeamColor() != teamColor) {continue;}

                Collection<ChessMove> teamPieceMoves = potentialTeammatePiece.pieceMoves(gameBoard, potentialTeammatePosition);
                if (!teamPieceMoves.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
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
