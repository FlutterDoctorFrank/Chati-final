package view2.userInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.Chati;
import view2.ChatiLocalization;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten TextTooltips repräsentiert.
 */
public class ChatiTooltip extends TextTooltip {

    private static final float DEFAULT_WRAP_WIDTH = 400;
    private final String tooltipKey;

    /**
     * Erzeugt eine neue Instanz eines ChatiTooltip.
     * @param tooltipKey Kennung des anzuzeigenden Text.
     */
    public ChatiTooltip(@NotNull final String tooltipKey) {
        super("", new ChatiTooltipStyle());
        this.tooltipKey = tooltipKey;
        setInstant(true);
    }

    @Override
    public void enter(@NotNull final InputEvent event, final float x, final float y, final int pointer,
                      @Nullable final Actor fromActor) {
        super.getActor().setText(Chati.CHATI.getLocalization().translate(tooltipKey));
        super.enter(event, x, y, pointer, fromActor);
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
