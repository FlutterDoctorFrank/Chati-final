package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import view2.Assets;
import view2.Chati;
import view2.component.AbstractWindow;

public class LanguageSelectWindow extends AbstractWindow {

    private static final float WINDOW_WIDTH = 550;
    private static final float WINDOW_HEIGHT = 350;
    private static final float ROW_WIDTH = 450;
    private static final float ROW_HEIGHT = 60;
    private static final float VERTICAL_SPACING = 15;
    private static final float HORIZONTAL_SPACING = 15;

    private Label infoLabel;
    private SelectBox<String> languageSelectBox;
    private TextButton confirmButton;
    private TextButton cancelButton;
    private TextButton closeButton;

    public LanguageSelectWindow() {
        super("Sprache auswählen");
        create();
        setLayout();
    }

    @Override
    protected void create() {
        this.infoLabel = new Label("Wähle eine Sprache aus!", Assets.SKIN);

        languageSelectBox = new SelectBox<>(Assets.SKIN);
        // Das kommt noch

        confirmButton = new TextButton("Bestätigen", Assets.SKIN);
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // Das kommt noch
                close();
            }
        });

        cancelButton = new TextButton("Abbrechen", Assets.SKIN);
        cancelButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                close();
            }
        });

        closeButton = new TextButton("X", Assets.SKIN);
        closeButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                close();
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

        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).width((ROW_WIDTH - HORIZONTAL_SPACING) / 2).height(ROW_HEIGHT);
        buttonContainer.add(confirmButton).spaceRight(HORIZONTAL_SPACING);
        buttonContainer.add(cancelButton);
        add(buttonContainer).width(ROW_WIDTH).height(ROW_HEIGHT);

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }
}
