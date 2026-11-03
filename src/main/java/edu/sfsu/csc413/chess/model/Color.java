package edu.sfsu.csc413.chess.model;

public enum Color {
    WHITE,
    BLACK;

    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }

    public int pawnDirection() {
        return this == WHITE ? 1 : -1;
    }

    public int pawnStartRank() {
        return this == WHITE ? 1 : 6;
    }

    public int promotionRank() {
        return this == WHITE ? 7 : 0;
    }
}