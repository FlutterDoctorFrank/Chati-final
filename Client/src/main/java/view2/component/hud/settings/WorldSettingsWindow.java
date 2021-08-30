package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import view2.Assets;
import view2.Settings;
import view2.component.AbstractWindow;
import view2.component.hud.HudMenuWindow;

public class WorldSettingsWindow extends AbstractWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 350;
    private static final float ROW_HEIGHT = 60;
    private static final float SPACING = 15;

    private Label infoLabel;
    private Label showNameLabel;
    private CheckBox showNameCheckBox;
    private TextButton confirmButton;
    private TextButton defaultButton;
    private TextButton cancelButton;
    private TextButton closeButton;

    public WorldSettingsWindow() {
        super("Welteinstellungen");
        create();
        setLayout();
    }

    @Override
    protected void create() {
        infoLabel = new Label("Führe Welteinstellungen durch.", Assets.SKIN);

        showNameLabel = new Label("Benutzernamen dauerhaft anzeigen.", Assets.SKIN);
        showNameCheckBox = new CheckBox("", Assets.SKIN);
        showNameCheckBox.setChecked(Settings.getShowNamesInWorld());
        showNameCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enableButton();
                infoLabel.setText("Führe Welteinstellungen durch.");
            }
        });

        confirmButton = new TextButton("Übernehmen", Assets.getNewSkin());
        disableButton();
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                disableButton();
                Settings.setShowNamesInWorld(showNameCheckBox.isChecked());
                infoLabel.setText("Deine Änderungen wurden gespeichert!");
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
                enableButton();
                showNameCheckBox.setChecked(Settings.DEFAULT_SHOW_NAMES_IN_WORLD);
                infoLabel.setText("Führe Welteinstellungen durch.");
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

        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        infoLabel.setAlignment(Align.center, Align.center);
        container.add(infoLabel).row();
        Table showNameContainer = new Table();
        showNameContainer.defaults().colspan(16).height(ROW_HEIGHT).space(SPACING).growX();
        showNameContainer.add(showNameLabel).colspan(15);
        showNameContainer.add(showNameCheckBox).colspan(1);
        container.add(showNameContainer).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(3).bottom().height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).padRight(SPACING / 2);
        buttonContainer.add(defaultButton).padLeft(SPACING / 2).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer);
        add(container).padLeft(SPACING).padRight(SPACING).grow();

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    private void enableButton() {
        confirmButton.setDisabled(false);
        confirmButton.setTouchable(Touchable.enabled);
        confirmButton.getLabel().setColor(Color.WHITE);
        confirmButton.getStyle().up = HudMenuWindow.UNPRESSED_BUTTON_IMAGE;
    }

    private void disableButton() {
        confirmButton.setDisabled(true);
        confirmButton.setTouchable(Touchable.disabled);
        confirmButton.getLabel().setColor(Color.DARK_GRAY);
        confirmButton.getStyle().up = HudMenuWindow.PRESSED_BUTTON_IMAGE;
    }
}
