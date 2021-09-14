package view2.userInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import view2.Chati;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten TextTooltips repräsentiert.
 */
public class ChatiTooltip extends TextTooltip {

    private static final float DEFAULT_WRAP_WIDTH = 400;

    /**
     * Erzeugt eine neue Instanz eines ChatiTooltip.
     * @param text Anzuzeigender Text.
     */
    public ChatiTooltip(String text) {
        super(text, new ChatiTooltipStyle());
        setInstant(true);
    }

    /**
     * Eine Klasse, welche den Style des ChatiTooltip repräsentiert.
     */
    private static class ChatiTooltipStyle extends TextTooltipStyle {

        /**
         * Erzeugt eine neue Instanz des ChatiTooltipStyle.
         */
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
