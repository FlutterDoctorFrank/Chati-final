package view2.component.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import view2.component.ChatiWindow;
import view2.component.ChatiTextButton;

public abstract class HudMenuWindow extends ChatiWindow {

    protected static final float HUD_WINDOW_WIDTH = 500;
    protected static final float HUD_WINDOW_HEIGHT = 600;

    protected HudMenuWindow(String title) {
        super(title);
        setMovable(false);
        setSize(HUD_WINDOW_WIDTH, HUD_WINDOW_HEIGHT);
        setPosition(Gdx.graphics.getWidth() - HUD_WINDOW_WIDTH,
                Gdx.graphics.getHeight() - HUD_WINDOW_HEIGHT - HeadUpDisplay.BUTTON_SIZE);
    }

    protected void enableButton(ChatiTextButton button) {
        button.setDisabled(false);
        button.setTouchable(Touchable.enabled);
        button.getLabel().setColor(Color.WHITE);
    }

    protected void disableButton(ChatiTextButton button) {
        button.setDisabled(true);
        button.setTouchable(Touchable.disabled);
        button.getLabel().setColor(Color.DARK_GRAY);
    }

    @Override
    public void close() {
        HeadUpDisplay.getInstance().removeCurrentMenu();
        super.close();
    }
}
