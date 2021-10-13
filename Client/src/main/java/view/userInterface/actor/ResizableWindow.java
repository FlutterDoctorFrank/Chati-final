package view.userInterface.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.Chati;
import view.ChatiCursor;

/**
 * Eine Klasse, welche ein größenveränderbares Fenster repräsentiert.
 */
public abstract class ResizableWindow extends Window {

    private static final int RESIZE_BORDER = 8;

    private boolean resizeCursor;

    /**
     * Erzeugt eine neue Instanz des ResizableWindow.
     * @param title Anzuzeigender Titel.
     */
    public ResizableWindow(@NotNull final String title) {
        super(title, Chati.CHATI.getSkin());

        setModal(false);
        setMovable(true);
        setResizable(true);
        setResizeBorder(RESIZE_BORDER);
        setKeepWithinStage(true);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(@NotNull final InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(@NotNull final InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
                    Gdx.graphics.setCursor(ChatiCursor.ARROW.getCursor());
                    resizeCursor = false;
                }
            }

            @Override
            public void enter(@NotNull final InputEvent event, final float x, final float y, final int pointer,
                              @Nullable final Actor fromActor) {
                if (pointer == -1) {
                    setCursor(x, y);
                }
            }

            @Override
            public void exit(@NotNull final InputEvent event, final float x, final float y, final int pointer,
                             @Nullable final Actor toActor) {
                if (pointer == -1) {
                    setCursor(x, y);
                }
            }

            @Override
            public boolean mouseMoved(@NotNull final InputEvent event, final float x, final float y) {
                setCursor(x, y);
                return true;
            }

            /**
             * Setzt die Cursor zur Anzeige der Größenskalierbarkeit.
             * @param x X-Koordinate des Cursors.
             * @param y Y-Koordinate des Cursors.
             */
            private void setCursor(final float x, final float y) {
                if (!hasParent() || !isVisible() || !isResizable()) {
                    Gdx.graphics.setCursor(ChatiCursor.ARROW.getCursor());
                    resizeCursor = false;
                    return;
                }
                float border = RESIZE_BORDER / 2f;
                boolean resizeLeft = x >= getPadLeft() - border && x < getPadLeft() + border;
                boolean resizeRight = x > getWidth() - getPadRight() - border
                        && x <= getWidth() - getPadRight() + border;
                boolean resizeBottom = y >= getPadBottom() - border && y < getPadBottom() + border;
                if (resizeLeft && resizeBottom) {
                    Gdx.graphics.setCursor(ChatiCursor.RISING_DIAGONAL_RESIZE.getCursor());
                    resizeCursor = true;
                } else if (resizeRight && resizeBottom) {
                    Gdx.graphics.setCursor(ChatiCursor.FALLING_DIAGONAL_RESIZE.getCursor());
                    resizeCursor = true;
                } else if (resizeLeft || resizeRight) {
                    Gdx.graphics.setCursor(ChatiCursor.HORIZONTAL_RESIZE.getCursor());
                    resizeCursor = true;
                } else if (resizeBottom) {
                    Gdx.graphics.setCursor(ChatiCursor.VERTICAL_RESIZE.getCursor());
                    resizeCursor = true;
                } else if (resizeCursor) {
                    Gdx.graphics.setCursor(ChatiCursor.ARROW.getCursor());
                    resizeCursor = false;
                }
            }
        });
    }
}
