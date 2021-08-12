package view2.component;

import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import view2.Chati;


public class ChatiToolTip extends TextTooltip {

    public ChatiToolTip(String text) {
        super(text, new ChatiTooltipManager(), Chati.SKIN);
    }

    private static class ChatiTooltipManager extends TooltipManager {
        private ChatiTooltipManager() {
            instant();
        }
    }
}
