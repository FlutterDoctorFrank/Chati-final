package view.userInterface.interactableMenu.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.Chati;
import view.ChatiLocalization.Translatable;
import view.userInterface.actor.ChatiLabel;
import view.userInterface.actor.ChatiSelectBox;
import view.userInterface.actor.ChatiTextButton;
import view.userInterface.interactableMenu.GameBoardWindow;
import view.userInterface.interactableMenu.game.TicTacToeTable.TicTacToeGame.Difficulty;
import view.userInterface.interactableMenu.game.TicTacToeTable.TicTacToeGame.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Eine Klasse, welche das Menü des Tic-Tac-Toe Spiels repräsentiert.
 */
public class TicTacToeTable extends GameTable {

    private static final float FIELD_SPACING = 5f;
    private static final float FIELD_SIZE = 75f;

    private final TicTacToeField[] board = new TicTacToeField[9];
    private final ChatiSelectBox<Difficulty> difficultyBox;
    private final ChatiTextButton newGameButton;
    private final ChatiTextButton exitGameButton;

    private TicTacToeGame ticTacToe;

    /**
     * Erzeugt eine neue Instanz des TicTacToeTable.
     */
    public TicTacToeTable(@NotNull final GameBoardWindow window) {
        super(window, "table.entry.tic-tac-toe");

        ChatiLabel difficultyLabel = new ChatiLabel("menu.label.difficulty");

        difficultyBox = new ChatiSelectBox<>(Difficulty::getName);
        difficultyBox.setItems(Difficulty.values());
        difficultyBox.setSelected(Difficulty.NORMAL);

        newGameButton = new ChatiTextButton("menu.button.new-game", true);
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (ticTacToe == null || !ticTacToe.isRunning()) {
                    showMessage("menu.label.turn");
                    ticTacToe = new TicTacToeGame(difficultyBox.getSelected());
                    disableSelectBox(difficultyBox);
                    disableButton(newGameButton);
                    enableButton(exitGameButton);
                    resetTextFields();
                }
            }
        });

        exitGameButton = new ChatiTextButton("menu.button.exit-game", true);
        exitGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (ticTacToe != null && ticTacToe.isRunning()) {
                    infoLabel.translate();
                    ticTacToe = null;
                    enableSelectBox(difficultyBox);
                    enableButton(newGameButton);
                    disableButton(exitGameButton);
                }
            }
        });

        disableButton(exitGameButton);

        ChatiTextButton cancelButton = new ChatiTextButton("menu.button.cancel", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                close();
            }
        });

        Table gameboard = new Table();

        for (int index = 0; index < board.length; index++) {
            board[index] = new TicTacToeField(index);

            Cell<TicTacToeField> cell = gameboard.add(board[index]).size(FIELD_SIZE).space(FIELD_SPACING);

            if (index % 3 == 2) {
                cell.row();
            }
        }

        // Layout
        Table container = new Table();
        Table left = new Table();
        Table right = new Table();

        left.add(infoLabel).growX().spaceBottom(SPACING).row();
        left.add(gameboard).center();
        right.defaults().space(SPACING / 2).center().growX();
        right.add(difficultyLabel).row();
        right.add(difficultyBox).spaceBottom(SPACING).row();
        right.defaults().height(ROW_HEIGHT);
        right.add(newGameButton).row();
        right.add(exitGameButton).row();
        right.add(cancelButton);

        container.add(left).width(440f);
        container.add(right).width(250f);

        add(container).grow();

        // Translatable register
        translatables.add(difficultyLabel);
        translatables.add(newGameButton);
        translatables.add(exitGameButton);
        translatables.add(cancelButton);
    }

    public void setWinner() {
        if (ticTacToe == null) {
            return;
        }

        if (!ticTacToe.isRunning()) {
            if (ticTacToe.getWinner() != null) {
                showMessage(ticTacToe.getWinner() == Player.PLAYER ? "menu.label.won" : "menu.label.lost");
            } else {
                showMessage("menu.label.draw");
            }

            enableSelectBox(difficultyBox);
            enableButton(newGameButton);
            disableButton(exitGameButton);
        } else {
            showMessage("menu.label.turn");
        }
    }

    @Override
    public void translate() {
        super.translate();
        setWinner();
    }

    @Override
    public void resetTextFields() {
        for (final TicTacToeField field : this.board) {
            field.clearChildren();
        }
    }

    /**
     * Eine Klasse, welches ein Feld im Tic-Tac-Toe Spiel repräsentiert.
     */
    private class TicTacToeField extends TextButton {

        public TicTacToeField(final int index) {
            super("", Chati.CHATI.getSkin(), "field");

            this.setDisabled(false);
            this.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    // Das Spiel wurde nicht gestartet, ist bereits zu Ende oder das Feld ist bereits belegt.
                    if (ticTacToe == null || !ticTacToe.isRunning() || ticTacToe.getField(index) != null) {
                        return;
                    }

                    ticTacToe.place(index);

                    final Player[] fields = ticTacToe.getFields();

                    for (int index = 0; index < fields.length; index++) {
                        board[index].clearChildren();

                        if (fields[index] != null) {
                            Image image = null;

                            switch (fields[index]) {
                                case PLAYER:
                                    image = new Image(Chati.CHATI.getDrawable("tic-tac-toe_cross"));
                                    break;

                                case COMPUTER:
                                    image = new Image(Chati.CHATI.getDrawable("tic-tac-toe_circle"));
                                    break;
                            }

                            board[index].add(image).size(FIELD_SIZE - SPACING);
                        }
                    }

                    setWinner();
                }
            });
        }
    }

    /**
     * Eine Klasse, welche das Tic-Tac-Toe Spiel modelliert.
     */
    public static class TicTacToeGame {

        private static final Random RANDOM = new Random();

        private final Player[][] board;
        private final Difficulty difficulty;

        private Player winner;
        private boolean running;

        public TicTacToeGame(@NotNull final Difficulty difficulty) {
            this.board = new Player[3][3];
            this.difficulty = difficulty;
            this.running = true;
        }

        public void place(final int index) {
            if (this.getField(index) != null) {
                throw new IllegalStateException("Field is already occupied");
            }

            this.board[index % this.board.length][index / this.board.length] = Player.PLAYER;
            this.winner = this.checkWinner();

            // Der Spieler hat noch nicht gewonnen, der Computer ist nun dran.
            if (this.winner == null) {
                List<Move> moves = new ArrayList<>();

                for (int row = 0; row < this.board.length; row++) {
                    for (int col = 0; col < this.board.length; col++) {
                        if (this.board[row][col] == null) {
                            this.board[row][col] =Player.COMPUTER;
                            moves.add(new Move(row, col, this.checkScore(1)));
                            this.board[row][col] = null;
                        }
                    }
                }

                if (moves.isEmpty()) {
                    running = false;
                    return;
                }

                try {
                    System.out.println(Arrays.toString(moves.stream().map(Move::toString).toArray(String[]::new)));
                    final int maximum = moves.stream().mapToInt(Move::getScore).max().orElseThrow();

                    moves.removeIf(move -> move.getScore() != maximum);

                    final Move move = moves.get(RANDOM.nextInt(moves.size()));
                    this.board[move.getRow()][move.getCol()] = Player.COMPUTER;
                    this.winner = this.checkWinner();
                } catch (NoSuchElementException ex) {
                    throw new IllegalStateException("Unable to find place for the computer", ex);
                }
            }

            if (this.winner != null) {
                running = false;
            }
        }

        public boolean isRunning() {
            return this.running;
        }

        public @NotNull Player[] getFields() {
            final Player[] fields = new Player[this.board.length * this.board.length];
            int index = 0;

            for (int col = 0; col < this.board.length; col++) {
                for (int row = 0; row < this.board.length; row++) {
                    fields[index++] = this.board[row][col];
                }
            }

            return fields;
        }

        public @Nullable Player getField(final int index) {
            return this.board[index % this.board.length][index / this.board.length];
        }

        public @Nullable Player getWinner() {
            return this.winner;
        }

        private @Nullable Player checkWinner() {
            Player winner;

            for (int index = 0; index < this.board.length; index++) {
                if ((winner = this.board[index][0]) != null) {
                    for (int col = 1; col < this.board.length; col++) {
                        if (this.board[index][col] != winner) {
                            winner = null;
                            break;
                        }
                    }

                    if (winner != null) {
                        return winner;
                    }
                }

                if ((winner = this.board[0][index]) != null) {
                    for (int row = 1; row < this.board.length; row++) {
                        if (this.board[row][index] != winner) {
                            winner = null;
                            break;
                        }
                    }

                    if (winner != null) {
                        return winner;
                    }
                }
            }

            if ((winner = this.board[0][0]) != null) {
                for (int index = 1; index < this.board.length; index++) {
                    if (this.board[index][index] != winner) {
                        winner = null;
                        break;
                    }
                }

                if (winner != null) {
                    return winner;
                }
            }

            if ((winner = this.board[0][this.board.length - 1]) != null) {
                for (int index = 1; index < this.board.length; index++) {
                    if (this.board[index][this.board.length - 1 - index] != winner) {
                        winner = null;
                        break;
                    }
                }
            }

            return winner;
        }

        /*
         * Ermitteln den Score des Computers. Implementiert als MiniMax Algorithmus.
         */
        private int checkScore(int depth) {
            final Player winner = this.checkWinner();

            if (winner != null) {
                return winner == Player.COMPUTER ? 1 : -1;
            }

            if (depth++ < this.difficulty.getDifficulty()) {
                final List<Integer> scores = new ArrayList<>();

                for (int row = 0; row < this.board.length; row++) {
                    for (int col = 0; col < this.board.length; col++) {
                        if (this.board[row][col] == null) {
                            this.board[row][col] = depth % 2 == 0 ? Player.PLAYER : Player.COMPUTER;
                            scores.add(this.checkScore(depth));
                            this.board[row][col] = null;
                        }
                    }
                }

                if (!scores.isEmpty()) {
                    final int score = depth % 2 == 0 ? Collections.min(scores) : Collections.max(scores);

                    return score * Collections.frequency(scores, score);
                }
            }

            return 0;
        }

        /**
         * Eine Enumeration, welche einen Spieler des Tic-Tac-Toe Spiels repräsentiert.
         */
        public enum Player {

            PLAYER, COMPUTER

        }

        /**
         * Eine Klasse, welche die Schwierigkeit bzw. die Stärke des Computers im Tic-Tac-Toe Spiel repräsentiert.
         */
        public enum Difficulty implements Translatable {

            /**
             * Leichte Schwierigkeitsstufe, welche durch den Wert 1 repräsentiert wird.
             * <p>
             *     Bei dieser Stufe, kann der Computer einen Zug nach vorne schauen, d.h. der Computer sieht, ob er
             *     mit seinem Zug gewinnen kann.
             * </p>
             */
            EASY("menu.label.easy", 1),

            /**
             * Mittlere Schwierigkeitsstufe, welche durch den Wert 2 repräsentiert wird.
             * <p>
             *     Bei dieser Stufe, kann der Computer zwei Züge nach vorne schauen, d.h. der Computer sieht, ob er
             *     mit seinem Zug gewinnen kann oder ob er mit seinem Zug das Gewinnen des Gegners verhindern kann.
             * </p>
             */
            NORMAL("menu.label.normal", 2),

            /**
             * Schwere Schwierigkeitsstufe, welche durch den Wert 3 repräsentiert wird.
             * <p>
             *     Bei dieser Stufe, kann der Computer drei Züge nach vorne schauen, d.h. der Computer sieht, ob er
             *     mit seinem Zug gewinnen kann, ob er mit seinem Zug das Gewinnen des Gegners verhindern kann oder ob
             *     er mit seinem darauffolgenden Zug gewinnen kann.
             * </p>
             */
            HARD("menu.label.hard", 3);

            private final int difficulty;
            private final String key;
            private String name;

            Difficulty(@NotNull final String key, final int difficulty) {
                this.difficulty = difficulty;
                this.key = key;
            }

            public @NotNull String getName() {
                if (this.name == null) {
                    translate();
                }
                return this.name;
            }

            public int getDifficulty() {
                return this.difficulty;
            }

            @Override
            public void translate() {
                this.name = Chati.CHATI.getLocalization().translate(this.key);
            }
        }

        /**
         * Eine Klasse, welche einen Zug des Computers repräsentiert.
         */
        private static class Move {

            private final int row;
            private final int col;
            private final int score;

            private Move(final int row, final int col, final int score) {
                this.row = row;
                this.col = col;
                this.score = score;
            }

            public int getRow() {
                return this.row;
            }

            public int getCol() {
                return this.col;
            }

            public int getScore() {
                return this.score;
            }

            public @NotNull String toString() {
                return "(Index: " + (this.row * 3 + this.col) + ", score: " + this.score + ")";
            }
        }
    }
}
