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
    private boolean wKingMoved;
    private boolean bKingMoved;
    private boolean wLRookMoved;
    private boolean wRRookMoved;
    private boolean bLRookMoved;
    private boolean bRRookMoved;

    public ChessGame() {

        teamTurn = TeamColor.WHITE;

        gameBoard = new ChessBoard();
        gameBoard.resetBoard();

        copyBoard = gameBoard.clone();
        usingCopyBoard = false;

        wKingMoved = false;
        bKingMoved = false;
        wLRookMoved = false;
        wRRookMoved = false;
        bLRookMoved = false;
        bRRookMoved = false;

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
        //if piece is king- check castling requirements (squares clear, king and thing haven't moved, not castling through check) then put the copyboard bakc
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
        //White king castle left
        if (color==TeamColor.WHITE) {
            if (startPosition.equals(new ChessPosition(1, 5))) {
                if (!wKingMoved && !wLRookMoved) {
                    //no pieces between them
                    if (gameBoard.getPiece(new ChessPosition(1, 2)) == null && gameBoard.getPiece(new ChessPosition(1, 3)) == null && gameBoard.getPiece(new ChessPosition(1, 4)) == null) {
                        //king isn't in check
                        if (!isInCheck(color)) {
                            //king isn't castling through check
                            usingCopyBoard = true;
                            copyBoard = gameBoard.clone();
                            copyBoard.addPiece(new ChessPosition(1, 4), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KING));
                            copyBoard.addPiece(new ChessPosition(1, 5), null);
                            if (!isInCheck(color)) {
                                copyBoard = gameBoard.clone();
                                copyBoard.addPiece(new ChessPosition(1, 3), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KING));
                                copyBoard.addPiece(new ChessPosition(1, 5), null);
                                if (!isInCheck(color)) {
                                    //make sure nothing is in 1, 2
                                    usingCopyBoard = false;
                                    if (gameBoard.getPiece(new ChessPosition(1, 2)) == null && gameBoard.getPiece(new ChessPosition(1, 3)) == null && gameBoard.getPiece(new ChessPosition(1, 4)) == null) {
                                        //then we can castle==== add just the king move and if the king moves two then move the castle two as well
                                        finalMoveList.add(new ChessMove(startPosition, new ChessPosition(1, 3), null));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            usingCopyBoard = false;
            //White king castle right
            if (startPosition.equals(new ChessPosition(1, 5))) {
                if (!wKingMoved && !wRRookMoved) {
                    //no pieces between them
                    if (gameBoard.getPiece(new ChessPosition(1, 7)) == null && gameBoard.getPiece(new ChessPosition(1, 6)) == null) {
                        //king isn't in check
                        if (!isInCheck(color)) {
                            //king isn't castling through check
                            usingCopyBoard = true;
                            copyBoard = gameBoard.clone();
                            copyBoard.addPiece(new ChessPosition(1, 6), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KING));
                            copyBoard.addPiece(new ChessPosition(1, 5), null);
                            if (!isInCheck(color)) {
                                copyBoard = gameBoard.clone();
                                copyBoard.addPiece(new ChessPosition(1, 7), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KING));
                                copyBoard.addPiece(new ChessPosition(1, 5), null);
                                if (!isInCheck(color)) {
                                    //make sure nothing is in 1, 2
                                    usingCopyBoard = false;
                                    if (gameBoard.getPiece(new ChessPosition(1, 6)) == null && gameBoard.getPiece(new ChessPosition(1, 7)) == null) {
                                        //then we can castle==== add just the king move and if the king moves two then move the castle two as well
                                        finalMoveList.add(new ChessMove(startPosition, new ChessPosition(1, 7), null));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            usingCopyBoard = false;
        }

        //Black king castle left
        if (color==TeamColor.BLACK) {
            if (startPosition.equals(new ChessPosition(8, 5))) {
                if (!bKingMoved && !bLRookMoved) {
                    //no pieces between them
                    if (gameBoard.getPiece(new ChessPosition(8, 2)) == null && gameBoard.getPiece(new ChessPosition(8, 3)) == null && gameBoard.getPiece(new ChessPosition(8, 4)) == null) {
                        //king isn't in check
                        if (!isInCheck(color)) {
                            //king isn't castling through check
                            usingCopyBoard = true;
                            copyBoard = gameBoard.clone();
                            copyBoard.addPiece(new ChessPosition(8, 4), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KING));
                            copyBoard.addPiece(new ChessPosition(8, 5), null);
                            if (!isInCheck(color)) {
                                copyBoard = gameBoard.clone();
                                copyBoard.addPiece(new ChessPosition(8, 3), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KING));
                                copyBoard.addPiece(new ChessPosition(8, 5), null);
                                if (!isInCheck(color)) {
                                    //make sure nothing is in 1, 2
                                    usingCopyBoard = false;
                                    if (gameBoard.getPiece(new ChessPosition(1, 2)) == null && gameBoard.getPiece(new ChessPosition(8, 3)) == null && gameBoard.getPiece(new ChessPosition(8, 4)) == null) {
                                        //then we can castle==== add just the king move and if the king moves two then move the castle two as well
                                        finalMoveList.add(new ChessMove(startPosition, new ChessPosition(8, 3), null));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            usingCopyBoard = false;
            //Black king castle right
        if (startPosition.equals(new ChessPosition(8, 5))) {
            if (!bKingMoved && !bRRookMoved) {
                //no pieces between them
                if (gameBoard.getPiece(new ChessPosition(8, 7)) == null && gameBoard.getPiece(new ChessPosition(8, 6)) == null) {
                    //king isn't in check
                    if (!isInCheck(color)) {
                        //king isn't castling through check
                        usingCopyBoard = true;
                        copyBoard = gameBoard.clone();
                        copyBoard.addPiece(new ChessPosition(8, 6), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KING));
                        copyBoard.addPiece(new ChessPosition(8, 5), null);
                        if (!isInCheck(color)) {
                            copyBoard = gameBoard.clone();
                            copyBoard.addPiece(new ChessPosition(8, 7), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KING));
                            copyBoard.addPiece(new ChessPosition(8, 5), null);
                            if (!isInCheck(color)) {
                                //make sure nothing is in 1, 2
                                usingCopyBoard = false;
                                if (gameBoard.getPiece(new ChessPosition(8, 6)) == null && gameBoard.getPiece(new ChessPosition(8, 7)) == null) {
                                    //then we can castle==== add just the king move and if the king moves two then move the castle two as well
                                    finalMoveList.add(new ChessMove(startPosition, new ChessPosition(8, 7), null));
                                }
                            }
                        }
                    }
                }
            }
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
                if (startPosition.equals(new ChessPosition(1, 5)) && endPosition.equals(new ChessPosition(1, 3)) && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    gameBoard.addPiece(new ChessPosition(1, 4), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                    gameBoard.addPiece(new ChessPosition(1, 1),null);
                    wKingMoved = true;
                    wLRookMoved = true;

                } else if (startPosition.equals(new ChessPosition(1, 5)) && endPosition.equals(new ChessPosition(1, 7)) && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    gameBoard.addPiece(new ChessPosition(1, 6), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                    gameBoard.addPiece(new ChessPosition(1, 8),null);
                    wKingMoved = true;
                    wRRookMoved = true;

                } else if (startPosition.equals(new ChessPosition(8, 5)) && endPosition.equals(new ChessPosition(8, 3)) && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    gameBoard.addPiece(new ChessPosition(8, 4), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                    gameBoard.addPiece(new ChessPosition(8, 1),null);
                    bKingMoved = true;
                    bLRookMoved = true;

                } else if (startPosition.equals(new ChessPosition(8, 5)) && endPosition.equals(new ChessPosition(8, 7)) && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    gameBoard.addPiece(new ChessPosition(8, 6), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                    gameBoard.addPiece(new ChessPosition(8, 8),null);
                    bKingMoved = true;
                    bRRookMoved = true;

                }
                gameBoard.addPiece(endPosition, piece);
            } else {
                gameBoard.addPiece(endPosition, new ChessPiece(teamTurn, move.getPromotionPiece()));
            }
            gameBoard.addPiece(startPosition, null);

            //check to see if kings or rooks have moved
            if (startPosition.equals(new ChessPosition(1, 1)) && piece.getPieceType().equals(ChessPiece.PieceType.ROOK)) {
                wLRookMoved = true;
            }
            if (startPosition.equals(new ChessPosition(1, 8)) && piece.getPieceType().equals(ChessPiece.PieceType.ROOK)) {
                wRRookMoved = true;
            }
            if (startPosition.equals(new ChessPosition(8, 1)) && piece.getPieceType().equals(ChessPiece.PieceType.ROOK)) {
                bLRookMoved = true;
            }
            if (startPosition.equals(new ChessPosition(8, 8)) && piece.getPieceType().equals(ChessPiece.PieceType.ROOK)) {
                bRRookMoved = true;
            }
            if (startPosition.equals(new ChessPosition(1, 5)) && piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
                wKingMoved = true;
            }
            if (startPosition.equals(new ChessPosition(8, 5)) && piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
                bKingMoved = true;
            }


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
