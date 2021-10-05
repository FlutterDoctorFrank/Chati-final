package view2.userInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.ChatiCursor;

/**
 * Eine Klasse, welche einen Listener repräsentiert, der den MausCursor ändert, wenn ein Fenster in der Größe verändert
 * werden kann.
 */
public class ResizeWindowListener extends InputListener {

    private final Window window;
    private final int resizeBorder;

    private boolean resizeCursor;

    /**
     * Erzeugt eine neue Instanz des ResizeWindowListener.
     * @param window Größenveränderbares Fenster.
     * @param resizeBorder Größe des Randbereichs, in dem sich der Cursor zum Verändern der Größe befinden muss.
     */
    public ResizeWindowListener(@NotNull final Window window, final int resizeBorder) {
        this.window = window;
        this.resizeBorder = resizeBorder;
        this.resizeCursor = false;
    }

    @Override
    public boolean touchDown(@NotNull final InputEvent event, float x, float y, int pointer, int button) {
        return true;
    }

    @Override
    public void touchUp(@NotNull final InputEvent event, float x, float y, int pointer, int button) {
        if (x < 0 || x >= window.getWidth() || y < 0 || y >= window.getHeight()) {
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
        if (!window.isResizable() || !window.isVisible()) {
            Gdx.graphics.setCursor(ChatiCursor.ARROW.getCursor());
            resizeCursor = false;
            return;
        }
        float border = resizeBorder / 2f;
        boolean resizeLeft = x >= window.getPadLeft() - border && x < window.getPadLeft() + border;
        boolean resizeRight = x > window.getWidth() - window.getPadRight() - border
                && x <= window.getWidth() - window.getPadRight() + border;
        boolean resizeBottom = y >= window.getPadBottom() - border && y < window.getPadBottom() + border;
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
}
