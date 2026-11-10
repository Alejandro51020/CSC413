package edu.sfsu.csc413.chess.engine;

import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates moves and determines the status of a chess position.
 */
public final class MoveGenerator {

    private MoveGenerator() {
    }

    /**
     * Every move {@code color} may legally play in the given position.
     *
     * <p>A move is legal only if it does not leave that side's own king
     * in check.
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

    /**
     * Returns the current status for the side whose turn it is.
     */
    public static GameStatus status(Board board, Color sideToMove) {
        boolean inCheck = isInCheck(board, sideToMove);
        boolean hasLegalMove = !legalMoves(board, sideToMove).isEmpty();

        if (inCheck && !hasLegalMove) {
            return GameStatus.CHECKMATE;
        }

        if (!inCheck && !hasLegalMove) {
            return GameStatus.STALEMATE;
        }

        if (inCheck) {
            return GameStatus.CHECK;
        }

        return GameStatus.IN_PROGRESS;
    }
}