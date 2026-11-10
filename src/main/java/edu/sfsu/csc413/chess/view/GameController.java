package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.PieceType;

import java.util.List;
import java.util.Optional;

/**
 * Connects the player's decisions to the game: carry out what was asked, then
 * ask the view to redraw.
 *
 * <p>This is the C in MVC. It is the only class that knows about both the
 * {@link Game} and the {@link BoardView}. It holds no game state of its own:
 * the model owns the position, the view owns the display, and the controller
 * owns only the conversation between them.
 *
 * <p>When you are done, this class mentions no {@code Scanner}, no
 * {@code System.out}, no window, and none of the words "undo", "moves", or
 * "quit". Those belong to the view and to {@link PlayerAction#parse}.
 */
public class GameController {

    private final Game game;
    private final BoardView view;
    private boolean stopped;

    public GameController(Game game, BoardView view) {
        this.game = game;
        this.view = view;
    }

    /** The game being played, so a view can read it in order to draw it. */
    public Game game() {
        return game;
    }

    /**
     * Greets the player, draws the opening position, and hands control to the
     * view with {@link BoardView#start}.
     */
    public void start() {
        view.showMessage("Welcome to CSC 413 Chess.");
        view.render(game);
        view.start(this);
    }

    /**
     * The ask-and-handle loop, for views that can wait for input.
     *
     * <p>Asks the view for the next action and handles it, until the player
     * quits or the game is over.
     */
    public void runLoop() {
        while (!stopped && !game.isGameOver()) {
            PlayerAction action = view.nextAction(game);
            handle(action);
        }
    }

    /**
     * Carries out one player action and redraws.
     */
    public void handle(PlayerAction action) {
        switch (action) {
            case PlayerAction.Move move -> play(move);
            case PlayerAction.Undo ignored -> takeBack();
            case PlayerAction.ShowMoves ignored -> showLegal();
            case PlayerAction.Quit ignored -> {
                stopped = true;
                view.showMessage("Goodbye.");
                view.close();
                return;
            }
        }

        view.render(game);

        if (game.isGameOver()) {
            announceResult();
            view.close();
        }
    }

    private void play(PlayerAction.Move action) {
        String notation = action.notation();

        Optional<Move> move = game.findLegalMove(notation);

        if (move.isEmpty() && notation.length() == 4) {
            PieceType promotion = view.promotionChoice(game);

            String promotionNotation =
                    notation + Character.toLowerCase(promotion.symbol());

            move = game.findLegalMove(promotionNotation);
        }

        if (move.isPresent()) {
            game.play(move.get());
        } else {
            view.showMessage(
                    "Not a legal move: '" + notation + "'.");
        }
    }

    private void takeBack() {
        if (game.undoLastMove().isEmpty()) {
            view.showMessage("Nothing to undo.");
        }
    }

    private void showLegal() {
        List<String> legal = game.legalMoves().stream()
                .map(Move::toString)
                .sorted()
                .toList();

        view.showMessage(
                legal.size()
                        + " legal moves: "
                        + String.join(" ", legal));
    }

    private void announceResult() {
        if (game.status() == GameStatus.CHECKMATE) {
            view.showMessage(
                    "Checkmate. "
                            + game.sideToMove().opposite()
                            + " wins.");
        } else if (game.status() == GameStatus.STALEMATE) {
            view.showMessage(
                    "Stalemate. The game is a draw.");
        }
    }
}