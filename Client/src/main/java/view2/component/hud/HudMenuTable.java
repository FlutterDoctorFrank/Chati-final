package view2.component.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import view2.Assets;
import view2.component.ChatiTable;

public abstract class HudMenuTable extends ChatiTable {

    public static final Drawable PRESSED_BUTTON_IMAGE = new TextButton("", Assets.SKIN).getStyle().down;
    public static final Drawable UNPRESSED_BUTTON_IMAGE = new TextButton("", Assets.SKIN).getStyle().up;

    protected void enableButton(TextButton button) {
        button.setDisabled(false);
        button.setTouchable(Touchable.enabled);
        button.getLabel().setColor(Color.WHITE);
        button.getStyle().up = UNPRESSED_BUTTON_IMAGE;
    }

    protected void disableButton(TextButton button) {
        button.setDisabled(true);
        button.setTouchable(Touchable.disabled);
        button.getLabel().setColor(Color.DARK_GRAY);
        button.getStyle().up = PRESSED_BUTTON_IMAGE;
    }

    protected void selectButton(TextButton button) {
        button.setChecked(true);
        button.getLabel().setColor(Color.MAGENTA);
        button.getStyle().up = PRESSED_BUTTON_IMAGE;
    }

    protected void unselectButton(TextButton button) {
        button.setChecked(false);
        button.getLabel().setColor(Color.WHITE);
        button.getStyle().up = UNPRESSED_BUTTON_IMAGE;
    }
}
