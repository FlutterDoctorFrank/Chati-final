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

    /**
     * Erzeugt eine neue Instanz des WeblinkClickListener.
     * @param label Label, in dem Weblinks durch einen Klick geöffnet werden sollen.
     * @param webLinkColor Farbe der Weblinks in dem Label.
     */
    public WeblinkClickListener(@NotNull final Label label, @NotNull final Color webLinkColor) {
        this.label = label;
        this.webLinkColor = webLinkColor;
    }

    @Override
    public void enter(@NotNull final InputEvent event, final float x, final float y, final int pointer,
                      @Nullable final Actor fromActor) {
        if (pointer == -1) {
            label.getGlyphLayout().runs.forEach(run -> {
                if (run.color.equals(webLinkColor)) {
                    Gdx.graphics.setCursor(ChatiCursor.HAND.getCursor());
                }
            });
        }
    }

    @Override
    public void exit(@NotNull final InputEvent event, final float x, final float y, final int pointer,
                     @Nullable final Actor fromActor) {
        if (pointer == -1) {
            label.getGlyphLayout().runs.forEach(run -> {
                if (run.color.equals(webLinkColor)) {
                    Gdx.graphics.setCursor(ChatiCursor.ARROW.getCursor());
                }
            });
        }
    }

    @Override
    public void clicked(@NotNull final InputEvent event, final float x, final float y) {
        Array<GlyphLayout.GlyphRun> runs = label.getGlyphLayout().runs;
        for (int i = 0; i < runs.size; i++) {
            float textHeight = label.getHeight() - 3;
            float lineHeight = label.getStyle().font.getLineHeight();
            if (x > runs.get(i).x && x < runs.get(i).x + runs.get(i).width && y > runs.get(i).y + textHeight
                    - lineHeight && y < runs.get(i).y + textHeight && runs.get(i).color.equals(webLinkColor)) {
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
                if (!Gdx.net.openURI(urlBuilder.toString())) {
                    Gdx.net.openURI("https://www.google.de/search?q=" + urlBuilder);
                }
            }
        }
    }
}
