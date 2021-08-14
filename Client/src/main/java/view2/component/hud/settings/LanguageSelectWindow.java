package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import view2.Chati;
import view2.component.ChatiWindow;

public class LanguageSelectWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 550;
    private static final float WINDOW_HEIGHT = 350;
    private static final float ROW_WIDTH = 450;
    private static final float ROW_HEIGHT = 60;
    private static final float VERTICAL_SPACING = 15;
    private static final float HORIZONTAL_SPACING = 15;
    private static final float LABEL_FONT_SCALE_FACTOR = 0.5f;

    private Label infoLabel;
    private SelectBox<String> languageSelectBox;
    private TextButton confirmButton;
    private TextButton cancelButton;

    public LanguageSelectWindow() {
        super("Sprache auswählen");
        create();
        setLayout();
    }

    @Override
    protected void create() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.font.getData().scale(LABEL_FONT_SCALE_FACTOR);
        this.infoLabel = new Label("Wähle eine Sprache aus!", style);

        languageSelectBox = new SelectBox<>(Chati.SKIN);
        // Das kommt noch

        confirmButton = new TextButton("Bestätigen", Chati.SKIN);
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // Das kommt noch
                remove();
            }
        });

        cancelButton = new TextButton("Abbrechen", Chati.SKIN);
        cancelButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                remove();
            }
        });
    }

    @Override
    protected void setLayout() {
        setModal(true);
        setMovable(false);
        setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
        setWidth(WINDOW_WIDTH);
        setHeight(WINDOW_HEIGHT);

        Table labelContainer = new Table();
        labelContainer.add(infoLabel).center();

        add(labelContainer).width(ROW_WIDTH).height(ROW_HEIGHT).spaceTop(VERTICAL_SPACING).spaceBottom(VERTICAL_SPACING).row();
        add(languageSelectBox).width(ROW_WIDTH).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).row();

        Table buttonContainer = new Table(Chati.SKIN);
        buttonContainer.add(confirmButton).width((ROW_WIDTH - VERTICAL_SPACING) / 2).height(ROW_HEIGHT).space(HORIZONTAL_SPACING);
        buttonContainer.add(cancelButton).width((ROW_WIDTH - VERTICAL_SPACING) / 2).height(ROW_HEIGHT);
        add(buttonContainer).width(ROW_WIDTH).height(ROW_HEIGHT);
    }
}
