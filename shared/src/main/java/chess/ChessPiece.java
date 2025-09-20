package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

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
        List<ChessMove> moves = new ArrayList<>();

        //bishop: all four diagonals until hit 1 or 8
        //up left: remove one from col and add one to row until either row = 1 or col = 8, repeat similar idea for the other 4 directions
        if (piece.getPieceType() == PieceType.BISHOP) {

            //up and left
            int newRow = myPosition.getRow()+1;
            int newCol = myPosition.getColumn()-1;
            while (newRow <= 8 && newCol >= 1) {
                if (board.getPiece(new ChessPosition(newRow, newCol)) != null) {
                    if (board.getPiece(new ChessPosition(newRow, newCol)).pieceColor == piece.pieceColor)
                        break;
                    else {
                        moves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(newRow, newCol), null));
                        break;
                    }
                } else {
                    moves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(newRow, newCol), null));
                    newRow++;
                    newCol--;
                }
            }

            //up and right
            newRow = myPosition.getRow()+1;
            newCol = myPosition.getColumn()+1;
            while (newRow <= 8 && newCol <= 8) {
                if (board.getPiece(new ChessPosition(newRow, newCol)) != null) {
                    if (board.getPiece(new ChessPosition(newRow, newCol)).pieceColor == piece.pieceColor)
                        break;
                    else {
                        moves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(newRow, newCol), null));
                        break;
                    }
                } else {
                    moves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(newRow, newCol), null));
                    newRow++;
                    newCol++;
                }
            }

            //down and left
            newRow = myPosition.getRow()-1;
            newCol = myPosition.getColumn()-1;
            while (newRow >= 1 && newCol >= 1) {
                if (board.getPiece(new ChessPosition(newRow, newCol)) != null) {
                    if (board.getPiece(new ChessPosition(newRow, newCol)).pieceColor == piece.pieceColor)
                        break;
                    else {
                        moves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(newRow, newCol), null));
                        break;
                    }
                } else {
                    moves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(newRow, newCol), null));
                    newRow--;
                    newCol--;
                }
            }

            //down and right
            newRow = myPosition.getRow()-1;
            newCol = myPosition.getColumn()+1;
            while (newRow >= 1 && newCol <= 8) {
                if (board.getPiece(new ChessPosition(newRow, newCol)) != null) {
                    if (board.getPiece(new ChessPosition(newRow, newCol)).pieceColor == piece.pieceColor)
                        break;
                    else {
                        moves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(newRow, newCol), null));
                        break;
                    }
                } else {
                    moves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(newRow, newCol), null));
                    newRow--;
                    newCol++;
                }
            }
        }

        if (piece.getPieceType() == PieceType.KING) {
            //do a while loop going through all 9 squares
            //each time on the loop if it's edge of board OR piece then pass accordingly to the next cycle
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    int newRow = myPosition.getRow() + i;
                    int newCol = myPosition.getColumn() + j;

                    //don't include current position or out of bounds positions
                    if (i == 0 && j == 0 || newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                        continue;
                    }

                    //if there's a piece there, move forward accordingly
                    if (board.getPiece(new ChessPosition(newRow, newCol)) != null && board.getPiece(new ChessPosition(newRow, newCol)).pieceColor == piece.pieceColor) {
                        continue;
                    }
                    moves.add(new ChessMove(new ChessPosition(myPosition.getRow(), myPosition.getColumn()), new ChessPosition(newRow, newCol), null));
                }
            }
        }
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
