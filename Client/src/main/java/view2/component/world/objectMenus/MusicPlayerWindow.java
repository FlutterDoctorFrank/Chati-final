package view2.component.world.objectMenus;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import model.context.ContextID;
import model.context.spatial.Music;
import view2.Chati;
import view2.component.ChatiWindow;

import java.util.EnumSet;

public class MusicPlayerWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 550;
    private static final float WINDOW_HEIGHT = 350;
    private static final float ROW_WIDTH = 450;
    private static final float ROW_HEIGHT = 60;
    private static final float VERTICAL_SPACING = 15;
    private static final float HORIZONTAL_SPACING = 15;
    private static final float LABEL_FONT_SCALE_FACTOR = 0.5f;

    private ContextID contextId;

    private Label infoLabel;
    private Label musicSelectLabel;
    private SelectBox<Music> musicSelectBox;
    private Image musicImage;
    private TextButton playButton;
    private TextButton pauseButton;
    private TextButton nextButton;
    private ProgressBar musicProgressBar;

    public MusicPlayerWindow() {
        super("Musik abspielen");
    }

    @Override
    protected void create() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.font.getData().scale(LABEL_FONT_SCALE_FACTOR);
        infoLabel = new Label("Wähle ein abzuspielendes Musikstück", style);

        musicSelectLabel = new Label("Musik: ", style);

        musicSelectBox = new SelectBox<>(Chati.SKIN);
        musicSelectBox.setItems(EnumSet.allOf(Music.class).toArray(new Music[0]));

        // TODO
    }

    @Override
    protected void setLayout() {

    }
}
