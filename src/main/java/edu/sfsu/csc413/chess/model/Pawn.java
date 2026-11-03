package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The pawn — the piece that breaks every rule the others follow.
 */
public class Pawn extends Piece {

    /** What a pawn may become on reaching the far rank. */
    private static final PieceType[] PROMOTION_CHOICES = {
            PieceType.QUEEN,
            PieceType.ROOK,
            PieceType.BISHOP,
            PieceType.KNIGHT
    };

    public Pawn(Color color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        List<Move> moves = new ArrayList<>();

        int direction = color().pawnDirection();

        Position oneForward = from.offsetOrNull(0, direction);

        if (oneForward != null && board.isEmpty(oneForward)) {
            addPawnMove(moves, from, oneForward, null);

            if (from.rank() == color().pawnStartRank()) {
                Position twoForward = from.offsetOrNull(0, 2 * direction);

                if (twoForward != null && board.isEmpty(twoForward)) {
                    moves.add(Move.quiet(from, twoForward, this));
                }
            }
        }

        int[] captureFiles = {-1, 1};

        for (int fileDelta : captureFiles) {
            Position targetPosition =
                    from.offsetOrNull(fileDelta, direction);

            if (targetPosition == null) {
                continue;
            }

            Piece target = board.pieceAt(targetPosition);

            if (target != null && target.color() != color()) {
                addPawnMove(moves, from, targetPosition, target);
            }
        }

        return moves;
    }

    private void addPawnMove(
            List<Move> moves,
            Position from,
            Position to,
            Piece captured) {

        if (to.rank() == color().promotionRank()) {
            for (PieceType promotionChoice : PROMOTION_CHOICES) {
                moves.add(
                        Move.promotion(
                                from,
                                to,
                                this,
                                captured,
                                promotionChoice));
            }
        } else if (captured == null) {
            moves.add(Move.quiet(from, to, this));
        } else {
            moves.add(Move.capture(from, to, this, captured));
        }
    }

    @Override
    public boolean attacks(
            Board board,
            Position from,
            Position target) {

        int direction = color().pawnDirection();

        Position leftAttack = from.offsetOrNull(-1, direction);
        Position rightAttack = from.offsetOrNull(1, direction);

        return target.equals(leftAttack)
                || target.equals(rightAttack);
    }
}