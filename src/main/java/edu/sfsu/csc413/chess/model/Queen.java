package edu.sfsu.csc413.chess.model;

import java.util.List;

/**
 * The queen: slides along both the straight and the diagonal directions.
 */
public class Queen extends Piece {

    /**
     * All eight directions: the four straight and the four diagonal.
     */
    private static final int[][] DIRECTIONS = {
            { 0,  1 },
            { 1,  0 },
            { 0, -1 },
            {-1,  0 },
            { 1,  1 },
            { 1, -1 },
            {-1, -1 },
            {-1,  1 }
    };

    public Queen(Color color) {
        super(color, PieceType.QUEEN);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        return slidingMoves(board, from, DIRECTIONS);
    }
}