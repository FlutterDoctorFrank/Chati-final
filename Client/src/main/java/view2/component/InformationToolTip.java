package view2.component;

import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import view2.Assets;

public class InformationToolTip extends TextTooltip {

    public InformationToolTip(String text) {
        super("   " + text + "   ", Assets.SKIN);
        setInstant(true);
    }
}
