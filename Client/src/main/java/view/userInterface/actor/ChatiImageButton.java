package view.userInterface.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.ChatiCursor;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten ImageButtons repräsentiert.
 */
public class ChatiImageButton extends ImageButton {

    private static final float DEFAULT_IMAGE_SCALE_FACTOR = 0.1f;
    private boolean underCursor;

    /**
     * Erzeugt eine neue Instanz eines ChatiImageButton.
     * @param imageUnchecked Im nicht ausgewählten Zustand anzuzeigendes Bild.
     * @param imageChecked Im ausgewählten Zustand anzuzeigendes Bild.
     * @param imageDisabled Im deaktivierten Zustand anzuzeigendes Bild.
     * @param imageScaleFactor Faktor, um den das Bild beim hovern beziehungsweise klicken vergrößert beziehungsweise
     * verkleinert werden soll.
     */
    public ChatiImageButton(@NotNull final Drawable imageUnchecked, @NotNull final Drawable imageChecked,
                            @NotNull final Drawable imageDisabled, final float imageScaleFactor) {
        super(imageUnchecked, imageUnchecked, imageChecked);
        getStyle().imageDisabled = imageDisabled;
        getImage().scaleBy(-imageScaleFactor);

        underCursor = false;
        addListener(new ClickListener() {
            @Override
            public boolean touchDown(@NotNull final InputEvent event, final float x, final float y,
                                     final int pointer, final int button) {
                getImage().scaleBy(-imageScaleFactor);
                return true;
            }
            @Override
            public void touchUp(@NotNull final InputEvent event, final float x, final float y,
                                final int pointer, final int button) {
                getImage().scaleBy(imageScaleFactor);
            }
            @Override
            public void enter(@NotNull final InputEvent event, final float x, final float y,
                              final int pointer, @Nullable final Actor fromActor) {
                if (pointer == -1) {
                    getImage().scaleBy(imageScaleFactor);
                    underCursor = true;
                }
            }
            @Override
            public void exit(@NotNull final InputEvent event, final float x, final float y,
                             final int pointer, @Nullable final Actor toActor) {
                if (pointer == -1) {
                    getImage().scaleBy(-imageScaleFactor);
                    underCursor = false;
                    Gdx.graphics.setCursor(ChatiCursor.ARROW.getCursor());
                }
            }
            @Override
            public boolean mouseMoved(@NotNull final InputEvent event, final float x, final float y) {
                if (underCursor) {
                    Gdx.graphics.setCursor(ChatiCursor.HAND.getCursor());
                }
                return true;
            }
        });
    }

    /**
     * Erzeugt eine neue Instanz eines ChatiImageButton.
     * @param imageUnchecked Im nicht ausgewählten Zustand anzuzeigendes Bild.
     * @param imageChecked Im ausgewählten Zustand anzuzeigendes Bild.
     * @param imageDisabled Im deaktivierten Zustand anzuzeigendes Bild.
     */
    public ChatiImageButton(@NotNull final Drawable imageUnchecked, @NotNull final Drawable imageChecked,
                            @NotNull final Drawable imageDisabled) {
        this(imageUnchecked, imageChecked, imageDisabled, DEFAULT_IMAGE_SCALE_FACTOR);
    }

    /**
     * Erzeugt eine neue Instanz eines ChatiImageButton.
     * @param imageUnchecked Im nicht ausgewählten und deaktivierten Zustand anzuzeigendes Bild.
     * @param imageChecked Im ausgewählten Zustand anzuzeigendes Bild.
     */
    public ChatiImageButton(@NotNull final Drawable imageUnchecked, @NotNull final Drawable imageChecked) {
        this(imageUnchecked, imageChecked, imageUnchecked, DEFAULT_IMAGE_SCALE_FACTOR);
    }

    /**
     * Erzeugt eine neue Instanz eines ChatiImageButton.
     * @param imageUnchecked Anzuzeigendes Bild.
     */
    public ChatiImageButton(@NotNull final Drawable imageUnchecked) {
        this(imageUnchecked, imageUnchecked, imageUnchecked, DEFAULT_IMAGE_SCALE_FACTOR);
    }
}
