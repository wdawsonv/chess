package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);


        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        }

        return List.of();
    }

    private static Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> finalMoveList = new ArrayList<>();

        finalMoveList.addAll(diagUpLeftMoves(board, myPosition));
        finalMoveList.addAll(diagUpRightMoves(board, myPosition));
        finalMoveList.addAll(diagDownLeftMoves(board, myPosition));
        finalMoveList.addAll(diagDownRightMoves(board, myPosition));

        return finalMoveList;
    }

    //Diagonal Move Checkers
    private static Collection<ChessMove> diagUpLeftMoves(ChessBoard board, ChessPosition myPosition) {

        int testRow = myPosition.getRow() + 1;
        int testCol = myPosition.getColumn() - 1;
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        while (testRow <= 8 && testCol >= 1) {
            ChessPosition testEndPosition = new ChessPosition(testRow, testCol);
            ChessMove possibleMove = new ChessMove(myPosition, testEndPosition, null);

            if (board.getPiece(testEndPosition) == null) {
                tempMoveList.add(possibleMove);
            } else {
                if (canCapture(board, myPosition, testEndPosition)) {
                    tempMoveList.add(possibleMove);
                }
                break;
            }
            testRow++;
            testCol--;
        }

        return tempMoveList;
    }
    private static Collection<ChessMove> diagUpRightMoves(ChessBoard board, ChessPosition myPosition) {
        int testRow = myPosition.getRow() + 1;
        int testCol = myPosition.getColumn() + 1;
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        while (testRow <= 8 && testCol <= 8) {
            ChessPosition testEndPosition = new ChessPosition(testRow, testCol);
            ChessMove possibleMove = new ChessMove(myPosition, testEndPosition, null);

            if (board.getPiece(testEndPosition) == null) {
                tempMoveList.add(possibleMove);
            } else {
                if (canCapture(board, myPosition, testEndPosition)) {
                    tempMoveList.add(possibleMove);
                }
                break;
            }
            testRow++;
            testCol++;
        }

        return tempMoveList;
    }
    private static Collection<ChessMove> diagDownLeftMoves(ChessBoard board, ChessPosition myPosition) {
        int testRow = myPosition.getRow() - 1;
        int testCol = myPosition.getColumn() - 1;
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        while (testRow >= 1 && testCol >= 1) {
            ChessPosition testEndPosition = new ChessPosition(testRow, testCol);
            ChessMove possibleMove = new ChessMove(myPosition, testEndPosition, null);

            if (board.getPiece(testEndPosition) == null) {
                tempMoveList.add(possibleMove);
            } else {
                if (canCapture(board, myPosition, testEndPosition)) {
                    tempMoveList.add(possibleMove);
                }
                break;
            }
            testRow--;
            testCol--;
        }

        return tempMoveList;
    }
    private static Collection<ChessMove> diagDownRightMoves(ChessBoard board, ChessPosition myPosition) {
        int testRow = myPosition.getRow() - 1;
        int testCol = myPosition.getColumn() + 1;
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        while (testRow >= 1 && testCol <= 8) {
            ChessPosition testEndPosition = new ChessPosition(testRow, testCol);
            ChessMove possibleMove = new ChessMove(myPosition, testEndPosition, null);

            if (board.getPiece(testEndPosition) == null) {
                tempMoveList.add(possibleMove);
            } else {
                if (canCapture(board, myPosition, testEndPosition)) {
                    tempMoveList.add(possibleMove);
                }
                break;
            }
            testRow--;
            testCol++;
        }

        return tempMoveList;
    }

    //General Helpers
    private static boolean canCapture(ChessBoard board, ChessPosition myPosition, ChessPosition testEndPosition) {
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        ChessGame.TeamColor otherColor = board.getPiece(testEndPosition).getTeamColor();
        return (myColor != otherColor);
    }
}
