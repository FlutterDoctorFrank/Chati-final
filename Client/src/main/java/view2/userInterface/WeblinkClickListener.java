package view2.userInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.ChatiCursor;

/**
 * Eine Klasse, welche einen ClickListener repräsentiert, der einen in einem Label enthaltenen Weblink durch einen
 * Klick öffnet. Die Farbe von Weblinks sollte sich hierbei von der Farbe des sonstigen Textes unterscheiden.
 */
public class WeblinkClickListener extends ClickListener {

    private final Label label;
    private final Color webLinkColor;
    private final float offsetY;

    private boolean cursorOverLink;

    /**
     * Erzeugt eine neue Instanz des WeblinkClickListener.
     * @param label Label, in dem Weblinks durch einen Klick geöffnet werden sollen.
     * @param webLinkColor Farbe der Weblinks in dem Label.
     */
    public WeblinkClickListener(@NotNull final Label label, @NotNull final Color webLinkColor, final float offsetY) {
        this.label = label;
        this.webLinkColor = webLinkColor;
        this.offsetY = offsetY;
        this.cursorOverLink = false;
    }

    @Override
    public void exit(@NotNull final InputEvent event, final float x, final float y, final int pointer,
                     @Nullable final Actor toActor) {
        if (pointer == -1) {
            Gdx.graphics.setCursor(ChatiCursor.ARROW.getCursor());
            cursorOverLink = false;
        }
    }

    @Override
    public boolean mouseMoved(@NotNull final InputEvent event, final float x, final float y) {
        float lineHeight = label.getStyle().font.getLineHeight() * label.getFontScaleY();
        Array<GlyphLayout.GlyphRun> runs = label.getGlyphLayout().runs;
        float offsetRunY = runs.get(runs.size - 1).y + offsetY;
        for (int i = 0; i < runs.size; i++) {
            if (x >= runs.get(i).x && x < runs.get(i).x + runs.get(i).width && y >= runs.get(i).y - offsetRunY
                    &&  y < runs.get(i).y - offsetRunY + lineHeight) {
                if (runs.get(i).color.equals(webLinkColor)) {
                    Gdx.graphics.setCursor(ChatiCursor.HAND.getCursor());
                    cursorOverLink = true;
                } else {
                    Gdx.graphics.setCursor(ChatiCursor.ARROW.getCursor());
                    cursorOverLink = false;
                }
                return true;
            }
        }
        Gdx.graphics.setCursor(ChatiCursor.ARROW.getCursor());
        cursorOverLink = false;
        return true;
    }

    @Override
    public void clicked(@NotNull final InputEvent event, final float x, final float y) {
        if (!cursorOverLink) {
            return;
        }
        float lineHeight = label.getStyle().font.getLineHeight() * label.getFontScaleY();
        Array<GlyphLayout.GlyphRun> runs = label.getGlyphLayout().runs;
        float offsetRunY = runs.get(runs.size - 1).y + offsetY;
        for (int i = 0; i < runs.size; i++) {
            if (cursorOverLink && x >= runs.get(i).x && x < runs.get(i).x + runs.get(i).width
                    && y >= runs.get(i).y - offsetRunY && y < runs.get(i).y - offsetRunY + lineHeight
                    && runs.get(i).color.equals(webLinkColor)) {
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(runs.get(i).glyphs.toString(""));
                urlBuilder.reverse();
                for (int j = i - 1; j > 0; j--) {
                    if (runs.get(j).color.equals(webLinkColor)) {
                        urlBuilder.append(new StringBuilder(runs.get(j).glyphs.toString("")).reverse());
                    } else {
                        break;
                    }
                }
                urlBuilder.reverse();
                for (int j = i + 1; j < runs.size; j++) {
                    if (runs.get(j).color.equals(webLinkColor)) {
                        urlBuilder.append(runs.get(j).glyphs.toString(""));
                    } else {
                        break;
                    }
                }
                Gdx.net.openURI(urlBuilder.toString());
            }
        }
    }
}
