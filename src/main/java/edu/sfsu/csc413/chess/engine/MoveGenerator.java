package edu.sfsu.csc413.chess.engine;

import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates the moves a side may play in a given position.
 *
 * <p>This class exists because of a question no single piece can answer. A
 * piece knows how it moves; it does not know the rule that applies to every
 * piece at once: <strong>you may not leave your own king attacked.</strong>
 * That rule needs the whole board, and it arrives in M5. This week the class
 * is where the generation loop lives, moved out of {@code Game}, so that when
 * the rule arrives it has a home.
 *
 * <p>Notice what this class is not. It holds no state: no board, no side to
 * move, no history. Everything it needs arrives as a parameter, so its methods
 * are {@code static} and it cannot be instantiated. Ask it the same question
 * about the same board twice and you get the same answer.
 *
 * <p>Nothing outside the {@code engine} package needs this class. The view and
 * {@code Main} talk to {@link Game}, which decides what "legal" means. That is
 * why one of the two methods below has no access modifier.
 */
public final class MoveGenerator {

    private MoveGenerator() {
    }

    /**
     * Every move {@code color} may legally play in the given position.
     *
     * <p>At M5 this filters pseudo-legal moves so that a move is kept only if
     * it does not leave that side's own king in check.
     */
    public static List<Move> legalMoves(Board board, Color color) {
        List<Move> legalMoves = new ArrayList<>();

        for (Move move : pseudoLegalMoves(board, color)) {
            board.apply(move);

            boolean kingIsSafe = !isInCheck(board, color);

            board.undo(move);

            if (kingIsSafe) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     * Every geometrically possible move for {@code color}, king safety aside.
     *
     * <p>This is the loop from session 6 §3 and M3's {@code Game.legalMoves()},
     * moved here unchanged. Note how little it knows: it never asks what kind
     * of piece it is looking at. It asks each piece for its own moves.
     *
     * <p>Package-private on purpose. Only {@code Game} and this class's tests,
     * both in {@code engine}, may call it. A view that wants the moves asks
     * {@code Game}.
     */
    static List<Move> pseudoLegalMoves(Board board, Color color) {
        List<Move> moves = new ArrayList<>();

        for (Position position : board.positionsOf(color)) {
            Piece piece = board.pieceAt(position);
            moves.addAll(piece.pseudoLegalMoves(board, position));
        }

        return moves;
    }

    /**
     * Returns whether {@code target} is attacked by any piece of
     * {@code attacker}.
     */
    public static boolean isAttacked(
            Board board,
            Position target,
            Color attacker) {

        for (Position position : board.positionsOf(attacker)) {
            Piece piece = board.pieceAt(position);

            if (piece.attacks(board, position, target)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether {@code color}'s king is currently in check.
     */
    public static boolean isInCheck(Board board, Color color) {
        Position kingPosition = board.kingPosition(color);

        if (kingPosition == null) {
            return false;
        }

        return isAttacked(
                board,
                kingPosition,
                color.opposite());
    }
}