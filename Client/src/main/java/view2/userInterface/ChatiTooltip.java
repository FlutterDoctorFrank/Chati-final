package view2.userInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import model.MessageBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.Chati;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten TextTooltips repräsentiert.
 */
public class ChatiTooltip extends TextTooltip {

    private static final float DEFAULT_WRAP_WIDTH = 400;
    private MessageBundle tooltipBundle;

    /**
     * Erzeugt eine neue Instanz eines ChatiTooltip.
     * @param tooltipKey Kennung des anzuzeigenden Text.
     * @param tooltipArguments Argumente des anzuzeigenden Textes.
     */
    public ChatiTooltip(@NotNull final String tooltipKey, @NotNull final Object... tooltipArguments) {
        super("", new ChatiTooltipStyle());
        this.tooltipBundle = new MessageBundle(tooltipKey, tooltipArguments);
        setInstant(true);
    }

    /**
     * Erzeugt eine neue Instanz eines ChatiTooltip.
     */
    public ChatiTooltip() {
        super("", new ChatiTooltipStyle());
        setInstant(true);
    }

    @Override
    public void enter(@NotNull final InputEvent event, final float x, final float y, final int pointer,
                      @Nullable final Actor fromActor) {
        super.getActor().setText(Chati.CHATI.getLocalization().format(tooltipBundle.getMessageKey(), tooltipBundle.getArguments()));
        super.enter(event, x, y, pointer, fromActor);
    }

    /**
     * Setzt die anzuzeigende Nachricht des Tooltips.
     * @param tooltipKey Kennung des anzuzeigenden Textes.
     * @param tooltipArguments Argumente des anzuzeigenden Textes.
     */
    public void setMessage(@NotNull final String tooltipKey, @NotNull final Object... tooltipArguments) {
        this.tooltipBundle = new MessageBundle(tooltipKey, tooltipArguments);
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
