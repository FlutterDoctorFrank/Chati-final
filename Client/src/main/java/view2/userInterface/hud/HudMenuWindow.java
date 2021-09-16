package view2.userInterface.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiWindow;
import view2.userInterface.ChatiTextButton;

/**
 * Eine abstrakte Klasse, welche ein Menü des HeadUpDisplay repräsentiert.
 */
public abstract class HudMenuWindow extends ChatiWindow {

    protected static final float HUD_WINDOW_WIDTH = 500;
    protected static final float HUD_WINDOW_HEIGHT = 600;

    /**
     * Erzeugt eine neue Instanz des HudMenuWindow.
     * @param titleKey Kennung des anzuzeigenden Titels.
     */
    protected HudMenuWindow(@NotNull final String titleKey) {
        super(titleKey);
        setMovable(false);
        setSize(HUD_WINDOW_WIDTH, HUD_WINDOW_HEIGHT);
        setPosition(Gdx.graphics.getWidth() - HUD_WINDOW_WIDTH,
                Gdx.graphics.getHeight() - HUD_WINDOW_HEIGHT - HeadUpDisplay.BUTTON_SIZE);
    }

    /**
     * Aktiviert einen Button.
     * @param button Zu aktivierender Button.
     */
    protected void enableButton(@NotNull final ChatiTextButton button) {
        button.setDisabled(false);
        button.setTouchable(Touchable.enabled);
        button.getLabel().setColor(Color.WHITE);
    }

    /**
     * Deaktiviert einen Button.
     * @param button Zu deaktivierender Button.
     */
    protected void disableButton(@NotNull final ChatiTextButton button) {
        button.setDisabled(true);
        button.setTouchable(Touchable.disabled);
        button.getLabel().setColor(Color.DARK_GRAY);
    }

    @Override
    public void close() {
        Chati.CHATI.getHeadUpDisplay().removeCurrentMenu();
        super.close();
    }
}
