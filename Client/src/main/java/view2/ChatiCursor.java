package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import org.jetbrains.annotations.NotNull;

public enum ChatiCursor {

    ARROW("arrow"),
    HAND("hand"),
    IBEAM("ibeam"),
    VERTICAL_RESIZE("vertical_resize"),
    HORIZONTAL_RESIZE("horizontal_resize"),
    RISING_DIAGONAL_RESIZE("rising_diagonal_resize"),
    FALLING_DIAGONAL_RESIZE("falling_diagonal_resize");

    private final Cursor cursor;

    ChatiCursor(@NotNull final String cursorImageName) {
        Pixmap cursorImage = Chati.CHATI.getPixmap(cursorImageName);
        this.cursor = Gdx.graphics.newCursor(cursorImage, cursorImage.getWidth() / 2, cursorImage.getHeight() / 2);
    }

    public @NotNull Cursor getCursor() {
        return cursor;
    }
}
