package view2.component;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import view2.Chati;

public class ChatiTextButton extends TextButton {

    public ChatiTextButton(String text, boolean disabled) {
        super(text, Chati.CHATI.getAssetManager().getSkin());
        setDisabled(disabled);
    }
}
