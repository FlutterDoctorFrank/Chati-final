package view2.userInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.ChatiLocalization.Translatable;
import java.util.ArrayList;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten Windows repräsentiert.
 */
public abstract class ChatiWindow extends Window implements IChatiWindow, Translatable {

    protected static final float ROW_HEIGHT = 60;
    protected static final float SPACE = 15;

    protected final ArrayList<Translatable> translatables;
    protected ChatiLabel infoLabel;
    protected String titleKey;

    protected float width;
    protected float height;

    /**
     * Erzeugt eine neue Instanz eines ChatiWindow.
     * @param titleKey Kennung des anzuzeigenden Titels.
     */
    protected ChatiWindow(@NotNull final String titleKey) {
        this(titleKey, -1f, -1f);
    }

    /**
     * Erzeugt eine neue Instanz eines ChatiWindow.
     * @param titleKey Kennung des anzuzeigenden Titels.
     * @param width Die Breite des Windows.
     * @param height Die Höhe des Windows.
     */
    protected ChatiWindow(@NotNull final String titleKey, final float width, final float height) {
        super(!titleKey.isBlank() ? Chati.CHATI.getLocalization().translate(titleKey) : "", Chati.CHATI.getSkin());
        this.translatables = new ArrayList<>();
        this.titleKey = titleKey;
        this.width = width;
        this.height = height;

        TextButton closeButton = new TextButton("X", Chati.CHATI.getSkin());
        closeButton.setDisabled(true);
        closeButton.addListener(new ChatiTooltip("hud.tooltip.close"));
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                close();
            }
        });

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));

        if (width > 0 && height > 0) {
            setWidth(width);
            setHeight(height);
        }
    }

    @Override
    public void invalidate() {
        if (width > 0 && height > 0) {
            setPosition((Gdx.graphics.getWidth() - width) / 2f, (Gdx.graphics.getHeight() - height) / 2f);
        }
    }

    @Override
    public void setWidth(final float width) {
        this.width = width;
        super.setWidth(width);
    }

    @Override
    public void setHeight(final float height) {
        this.height = height;
        super.setHeight(height);
    }

    @Override
    public void open() {
        Chati.CHATI.getScreen().getStage().openWindow(this);
    }

    @Override
    public void close() {
        Chati.CHATI.getScreen().getStage().closeWindow(this);
    }

    @Override
    public abstract void focus();

    /**
     * Zeigt eine Nachricht auf dem Info-Label an.
     * @param messageKey Kennung der anzuzeigenden Nachricht.
     */
    public void showMessage(@NotNull final String messageKey) {
        if (infoLabel != null) {
            infoLabel.setText(Chati.CHATI.getLocalization().translate(messageKey));
        }
    }

    /**
     * Zeigt eine Nachricht auf dem Info-Label an.
     * @param messageKey Kennung der anzuzeigenden Nachricht.
     * @param arguments Argumente der anzuzeigenden Nachricht.
     */
    public void showMessage(@NotNull final String messageKey, @NotNull final Object... arguments) {
        if (infoLabel != null) {
            infoLabel.setText(Chati.CHATI.getLocalization().format(messageKey, arguments));
        }
    }

    @Override
    public void translate() {
        getTitleLabel().setText(!titleKey.isBlank() ? Chati.CHATI.getLocalization().translate(titleKey) : "");
        translatables.forEach(Translatable::translate);
    }
}