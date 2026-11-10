package edu.sfsu.csc413.chess.engine;

import edu.sfsu.csc413.chess.factory.BoardFactory;
import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game {

    private final Board board;

    private final List<Move> history = new ArrayList<>();

    private Color sideToMove;

    public Game() {
        this(BoardFactory.standard(), Color.WHITE);
    }

    public Game(Board board, Color sideToMove) {
        this.board = board;
        this.sideToMove = sideToMove;
    }

    public Board board() {
        return board;
    }

    public Color sideToMove() {
        return sideToMove;
    }

    public List<Move> history() {
        return List.copyOf(history);
    }

    public List<Move> legalMoves() {
        return MoveGenerator.legalMoves(board, sideToMove);
    }

    public GameStatus status() {
        return MoveGenerator.status(board, sideToMove);
    }

    public boolean isGameOver() {
        return status().isGameOver();
    }

    public Optional<Move> findLegalMove(String notation) {
        for (Move move : legalMoves()) {
            if (move.toString().equalsIgnoreCase(notation)) {
                return Optional.of(move);
            }
        }

        return Optional.empty();
    }

    public void play(Move move) {
        if (!legalMoves().contains(move)) {
            throw new IllegalArgumentException(
                    "Illegal move: " + move);
        }

        board.apply(move);

        history.add(move);
        sideToMove = sideToMove.opposite();
    }

    public Optional<Move> undoLastMove() {
        if (history.isEmpty()) {
            return Optional.empty();
        }

        Move move = history.remove(history.size() - 1);

        board.undo(move);

        sideToMove = sideToMove.opposite();

        return Optional.of(move);
    }
}