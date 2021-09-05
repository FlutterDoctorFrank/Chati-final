package view2.component;

import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import view2.Chati;

public class ChatiToolTip extends TextTooltip {

    public ChatiToolTip(String text) {
        super("   " + text + "   ", Chati.CHATI.getSkin());
        setInstant(true);
    }
}
