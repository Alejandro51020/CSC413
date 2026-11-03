package edu.sfsu.csc413.chess.model;

/**
 * A single move: which piece went where, and what happened as a result.
 *
 * @param from        the square the piece left
 * @param to          the square the piece arrived on
 * @param moved       the piece that moved
 * @param captured    the piece removed by this move, or null for a quiet move
 * @param promotesTo  the type a pawn became, or null if this is not a promotion
 */
public record Move(
        Position from,
        Position to,
        Piece moved,
        Piece captured,
        PieceType promotesTo) {

    /** A move to an empty square. */
    public static Move quiet(Position from, Position to, Piece moved) {
        return new Move(from, to, moved, null, null);
    }

    /** A move that removes an enemy piece from the destination square. */
    public static Move capture(
            Position from,
            Position to,
            Piece moved,
            Piece captured) {

        return new Move(from, to, moved, captured, null);
    }

    /** A pawn reaching the far rank and becoming promotesTo. */
    public static Move promotion(
            Position from,
            Position to,
            Piece moved,
            Piece captured,
            PieceType promotesTo) {

        return new Move(from, to, moved, captured, promotesTo);
    }

    public boolean isCapture() {
        return captured != null;
    }

    public boolean isPromotion() {
        return promotesTo != null;
    }

    @Override
    public String toString() {
        String result = from.toString() + to.toString();

        if (isPromotion()) {
            result += Character.toLowerCase(promotesTo.symbol());
        }

        return result;
    }
}