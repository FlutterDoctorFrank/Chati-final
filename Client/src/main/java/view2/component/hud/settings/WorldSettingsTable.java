package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import view2.Assets;
import view2.Chati;
import view2.component.ChatiWindow;

public class WorldSettingsTable extends ChatiWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 350;
    private static final float ROW_WIDTH = 650;
    private static final float ROW_HEIGHT = 60;
    private static final float VERTICAL_SPACING = 15;
    private static final float HORIZONTAL_SPACING = 15;

    private static final boolean SHOW_NAME_DEFAULT = true;

    private static boolean CHANGED = false;
    public static boolean SHOW_NAME = SHOW_NAME_DEFAULT;

    private Label infoLabel;
    private Label showNameLabel;
    private CheckBox showNameCheckBox;
    private TextButton confirmButton;
    private TextButton defaultButton;
    private TextButton cancelButton;
    private TextButton closeButton;

    public WorldSettingsTable() {
        super("Welteinstellungen");
        create();
        setLayout();
    }

    @Override
    protected void create() {
        infoLabel = new Label("Führe Welteinstellungen durch.", Assets.SKIN);

        showNameLabel = new Label("Benutzernamen dauerhaft anzeigen.", Assets.SKIN);
        showNameCheckBox = new CheckBox("", Assets.SKIN);
        showNameCheckBox.setChecked(SHOW_NAME);

        confirmButton = new TextButton("Bestätigen", Assets.SKIN);
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                CHANGED = true;
                SHOW_NAME = showNameCheckBox.isChecked();
                Chati.CHATI.getScreen().getStage().closeWindow(WorldSettingsTable.this);
            }
        });

        defaultButton = new TextButton("Standardeinstellung", Assets.SKIN);
        defaultButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showNameCheckBox.setChecked(SHOW_NAME_DEFAULT);
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
                Chati.CHATI.getScreen().getStage().closeWindow(WorldSettingsTable.this);
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
                Chati.CHATI.getScreen().getStage().closeWindow(WorldSettingsTable.this);
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

        Table showNameContainer = new Table();
        showNameContainer.defaults().colspan(2).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).expandX();
        showNameContainer.add(showNameLabel).padLeft(2 * HORIZONTAL_SPACING);
        showNameCheckBox.getImage().scaleBy(0.5f);
        showNameContainer.add(showNameCheckBox).right().padRight(2 * HORIZONTAL_SPACING);
        add(showNameContainer).width(ROW_WIDTH).spaceTop(VERTICAL_SPACING).spaceBottom(2 * VERTICAL_SPACING).row();

        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(3).bottom().height(ROW_HEIGHT).space(HORIZONTAL_SPACING).growX();
        buttonContainer.add(confirmButton, defaultButton, cancelButton);
        add(buttonContainer).width(ROW_WIDTH).height(ROW_HEIGHT);

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }
}
