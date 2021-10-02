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
    public void exit(@NotNull InputEvent event, final float x, final float y, final int pointer,
                     @Nullable final Actor fromActor) {
        if (pointer == -1) {
            Gdx.graphics.setCursor(ChatiCursor.ARROW.getCursor());
        }
    }
    @Override
    public boolean mouseMoved(@NotNull final InputEvent event, final float x, final float y) {
        boolean leftResizeBorder = x >= 0 && x < resizeBorder;
        boolean rightResizeBorder = x > window.getWidth() - resizeBorder && x <= window.getWidth();
        boolean bottomResizeBorder = y >= 0 && y < resizeBorder;
        if (leftResizeBorder && bottomResizeBorder) {
            Gdx.graphics.setCursor(ChatiCursor.RISING_DIAGONAL_RESIZE.getCursor());
            resizeCursor = true;
        } else if (rightResizeBorder && bottomResizeBorder) {
            Gdx.graphics.setCursor(ChatiCursor.FALLING_DIAGONAL_RESIZE.getCursor());
            resizeCursor = true;
        } else if (leftResizeBorder || rightResizeBorder) {
            Gdx.graphics.setCursor(ChatiCursor.HORIZONTAL_RESIZE.getCursor());
            resizeCursor = true;
        } else if (bottomResizeBorder) {
            Gdx.graphics.setCursor(ChatiCursor.VERTICAL_RESIZE.getCursor());
            resizeCursor = true;
        } else if (resizeCursor) {
            Gdx.graphics.setCursor(ChatiCursor.ARROW.getCursor());
            resizeCursor = false;
        }
        return true;
    }
}
