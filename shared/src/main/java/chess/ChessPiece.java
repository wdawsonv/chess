package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        if (piece.getPieceType() == PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        }
        return List.of();
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> finalMoveList = new ArrayList<>();

        finalMoveList.addAll(diagUpLeftMoves(board, myPosition));
        finalMoveList.addAll(diagUpRightMoves(board, myPosition));
        finalMoveList.addAll(diagDownLeftMoves(board, myPosition));
        finalMoveList.addAll(diagDownRightMoves(board, myPosition));

        return finalMoveList;
    }

    //Diagonal Move Checkers
    private Collection<ChessMove> diagUpLeftMoves(ChessBoard board, ChessPosition myPosition) {
        int testRow = myPosition.getRow() + 1;
        int testCol = myPosition.getColumn() - 1;
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        while (testRow <= 8 && testCol >= 1) {
            ChessPosition testEndPosition = new ChessPosition(testRow, testCol);
            ChessMove possibleMove = new ChessMove(myPosition, testEndPosition, null);

            if (board.getPiece(testEndPosition) == null) {
                tempMoveList.add(possibleMove);
            }
            testRow++;
            testCol--;
        }

        return tempMoveList;
    }
    private Collection<ChessMove> diagUpRightMoves(ChessBoard board, ChessPosition myPosition) {
        int testRow = myPosition.getRow() + 1;
        int testCol = myPosition.getColumn() + 1;
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        while (testRow <= 8 && testCol <= 8) {
            ChessPosition testEndPosition = new ChessPosition(testRow, testCol);
            ChessMove possibleMove = new ChessMove(myPosition, testEndPosition, null);

            if (board.getPiece(testEndPosition) == null) {
                tempMoveList.add(possibleMove);
            }
            testRow++;
            testCol++;
        }

        return tempMoveList;
    }
    private Collection<ChessMove> diagDownLeftMoves(ChessBoard board, ChessPosition myPosition) {
        int testRow = myPosition.getRow() - 1;
        int testCol = myPosition.getColumn() - 1;
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        while (testRow >= 1 && testCol >= 1) {
            ChessPosition testEndPosition = new ChessPosition(testRow, testCol);
            ChessMove possibleMove = new ChessMove(myPosition, testEndPosition, null);

            if (board.getPiece(testEndPosition) == null) {
                tempMoveList.add(possibleMove);
            }
            testRow--;
            testCol--;
        }

        return tempMoveList;
    }
    private Collection<ChessMove> diagDownRightMoves(ChessBoard board, ChessPosition myPosition) {
        int testRow = myPosition.getRow() - 1;
        int testCol = myPosition.getColumn() + 1;
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        while (testRow >= 1 && testCol <= 8) {
            ChessPosition testEndPosition = new ChessPosition(testRow, testCol);
            ChessMove possibleMove = new ChessMove(myPosition, testEndPosition, null);

            if (board.getPiece(testEndPosition) == null) {
                tempMoveList.add(possibleMove);
            }
            testRow--;
            testCol++;
        }

        return tempMoveList;
    }
}
