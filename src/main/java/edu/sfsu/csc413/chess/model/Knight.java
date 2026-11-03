package edu.sfsu.csc413.chess.model;

import java.util.List;

/**
 * The knight: two squares along one axis, one along the other, jumping over
 * anything in between.
 */
public class Knight extends Piece {

    /**
     * The eight L-shapes, as {file, rank} deltas.
     */
    private static final int[][] OFFSETS = {
            { 1,  2 },
            { 2,  1 },
            { 2, -1 },
            { 1, -2 },
            {-1, -2 },
            {-2, -1 },
            {-2,  1 },
            {-1,  2 }
    };

    public Knight(Color color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        return steppingMoves(board, from, OFFSETS);
    }
}