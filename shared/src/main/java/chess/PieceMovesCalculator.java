package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);


        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return rookMoves(board, myPosition);
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return queenMoves(board, myPosition);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return knightMoves(board, myPosition);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return kingMoves(board, myPosition);
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return pawnMoves(board, myPosition);
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

    private static Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> finalMoveList = new ArrayList<>();

        finalMoveList.addAll(upMoves(board, myPosition));
        finalMoveList.addAll(downMoves(board, myPosition));
        finalMoveList.addAll(rightMoves(board, myPosition));
        finalMoveList.addAll(leftMoves(board, myPosition));

        return finalMoveList;
    }

    private static Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> finalMoveList = new ArrayList<>();

        finalMoveList.addAll(bishopMoves(board, myPosition));
        finalMoveList.addAll(rookMoves(board, myPosition));

        return finalMoveList;
    }

    private static Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> finalMoveList = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        Collection<ChessPosition> theoreticalEndPositions = new ArrayList<>(List.of(
                new ChessPosition(row+1, col+2),
                new ChessPosition(row+2, col+1),
                new ChessPosition(row-1, col+2),
                new ChessPosition(row-2, col+1),
                new ChessPosition(row+1, col-2),
                new ChessPosition(row+2, col-1),
                new ChessPosition(row-1, col-2),
                new ChessPosition(row-2, col-1)
                ));

        for (ChessPosition endPosition : theoreticalEndPositions) {
            if (isInBounds(endPosition)) {
                if (board.getPiece(endPosition) == null) {
                    finalMoveList.add(new ChessMove(myPosition, endPosition, null));
                } else {
                    if (canCapture(board, myPosition, endPosition)) {
                        finalMoveList.add(new ChessMove(myPosition, endPosition, null));
                    }
                }
            }
        }

        return finalMoveList;
    }

    private static Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> finalMoveList = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        Collection<ChessPosition> theoreticalEndPositions = new ArrayList<>();

        for (int i = row-1; i <= row+1; i++) {
            for (int j = col-1; j <= col+1; j++) {
                if (i == row && j == col) {
                    continue;
                }
                theoreticalEndPositions.add(new ChessPosition(i, j));
            }
        }

        for (ChessPosition endPosition : theoreticalEndPositions) {
            if (isInBounds(endPosition)) {
                if (board.getPiece(endPosition) == null) {
                    finalMoveList.add(new ChessMove(myPosition, endPosition, null));
                } else {
                    if (canCapture(board, myPosition, endPosition)) {
                        finalMoveList.add(new ChessMove(myPosition, endPosition, null));
                    }
                }
            }
        }

        return finalMoveList;
    }

    private static Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> finalMoveList = new ArrayList<>();

        finalMoveList.addAll(pawnForwardMoves(board, myPosition));
        finalMoveList.addAll(pawnCaptureMoves(board, myPosition));

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

    //Cardinal Move Checkers
    private static Collection<ChessMove> upMoves(ChessBoard board, ChessPosition myPosition) {

        int testRow = myPosition.getRow() + 1;
        int testCol = myPosition.getColumn();
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        while (testRow <= 8) {
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
        }

        return tempMoveList;
    }
    private static Collection<ChessMove> downMoves(ChessBoard board, ChessPosition myPosition) {

        int testRow = myPosition.getRow() - 1;
        int testCol = myPosition.getColumn();
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        while (testRow >= 1) {
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
        }

        return tempMoveList;
    }
    private static Collection<ChessMove> leftMoves(ChessBoard board, ChessPosition myPosition) {

        int testRow = myPosition.getRow();
        int testCol = myPosition.getColumn() - 1;
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        while (testCol >= 1) {
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
            testCol--;
        }

        return tempMoveList;
    }
    private static Collection<ChessMove> rightMoves(ChessBoard board, ChessPosition myPosition) {

        int testRow = myPosition.getRow();
        int testCol = myPosition.getColumn() + 1;
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        while (testCol <= 8) {
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
    private static boolean isInBounds(ChessPosition myPosition) {
        return (myPosition.getColumn() <= 8 && myPosition.getColumn() >= 1 && myPosition.getRow() <= 8 && myPosition.getRow() >= 1);
    }

    //Pawn Checkers
    private static Collection<ChessMove> pawnForwardMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (board.getPiece(new ChessPosition(row+1, col)) == null) {
                tempMoveList.addAll(pawnPromotionAdder(board, myPosition, new ChessPosition(row+1, col)));

                //check if it's the first move
                if (row == 2) {
                    if (board.getPiece(new ChessPosition(row+2, col)) == null) {
                        tempMoveList.add(new ChessMove(myPosition, new ChessPosition(row+2, col), null));
                    }
                }
            }
        } else {
            if (board.getPiece(new ChessPosition(row-1, col)) == null) {
                tempMoveList.addAll(pawnPromotionAdder(board, myPosition, new ChessPosition(row-1, col)));

                //check if it's the first move
                if (row == 7) {
                    if (board.getPiece(new ChessPosition(row-2, col)) == null) {
                        tempMoveList.add(new ChessMove(myPosition, new ChessPosition(row-2, col), null));
                    }
                }
            }
        }

        return tempMoveList;
    }
    private static Collection<ChessMove> pawnCaptureMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> tempMoveList = new ArrayList<>();
        Collection<ChessPosition> possibleCapturePositions = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //sort by team color & add possible moves
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            possibleCapturePositions.addAll(List.of(
                    new ChessPosition(row+1, col+1),
                    new ChessPosition(row+1, col-1)
                    ));
        } else if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            possibleCapturePositions.addAll(List.of(
                    new ChessPosition(row-1, col+1),
                    new ChessPosition(row-1, col-1)
            ));
        }

        for (ChessPosition endPosition : possibleCapturePositions) {
            if (endPosition.getColumn() < 1 || endPosition.getColumn() > 8) {
                continue;
            }
            if (board.getPiece(endPosition) == null) {
                continue;
            }
            if (canCapture(board, myPosition, endPosition)) {
                tempMoveList.addAll(pawnPromotionAdder(board, myPosition, endPosition));
            }
        }

        return tempMoveList;
    }
    private static Collection<ChessMove> pawnPromotionAdder(ChessBoard board, ChessPosition myPosition, ChessPosition endPosition) {
        Collection<ChessMove> tempMoveList = new ArrayList<>();

        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE && endPosition.getRow() == 8) {
            tempMoveList.addAll(List.of(
                    new ChessMove(myPosition, endPosition, ChessPiece.PieceType.QUEEN),
                    new ChessMove(myPosition, endPosition, ChessPiece.PieceType.KNIGHT),
                    new ChessMove(myPosition, endPosition, ChessPiece.PieceType.ROOK),
                    new ChessMove(myPosition, endPosition, ChessPiece.PieceType.BISHOP)
                    ));
        } else if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK && endPosition.getRow() == 1) {
            tempMoveList.addAll(List.of(
                    new ChessMove(myPosition, endPosition, ChessPiece.PieceType.QUEEN),
                    new ChessMove(myPosition, endPosition, ChessPiece.PieceType.KNIGHT),
                    new ChessMove(myPosition, endPosition, ChessPiece.PieceType.ROOK),
                    new ChessMove(myPosition, endPosition, ChessPiece.PieceType.BISHOP)
                    ));
        } else {
            tempMoveList.add(new ChessMove(myPosition, endPosition, null));
        }
        return tempMoveList;
    }
}
