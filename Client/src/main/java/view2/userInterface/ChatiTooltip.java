package view2.userInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import view2.Chati;

public class ChatiTooltip extends TextTooltip {

    private static final float DEFAULT_WRAP_WIDTH = 400;

    public ChatiTooltip(String text) {
        super(text, new ChatiTooltipStyle());
        setInstant(true);
    }

    private static class ChatiTooltipStyle extends TextTooltipStyle {

        public ChatiTooltipStyle() {
            NinePatch ninePatch = new NinePatch(Chati.CHATI.getSkin().getRegion("panel1"), 10, 10, 10, 10);
            ninePatch.setPadLeft(20);
            ninePatch.setPadRight(20);
            this.background = new NinePatchDrawable(ninePatch);
            this.label = new Label.LabelStyle(Chati.CHATI.getSkin().getFont("font-button"), Color.WHITE);
            this.wrapWidth = DEFAULT_WRAP_WIDTH;
        }
    }
}
