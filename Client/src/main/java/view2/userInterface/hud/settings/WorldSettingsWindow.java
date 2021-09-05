package view2.userInterface.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import view2.Chati;
import view2.ChatiPreferences;
import view2.userInterface.ChatiWindow;
import view2.userInterface.ChatiTextButton;

public class WorldSettingsWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 350;

    private final ChatiTextButton confirmButton;

    public WorldSettingsWindow() {
        super("Welteinstellungen");

        Label infoLabel = new Label("Führe Welteinstellungen durch.", Chati.CHATI.getSkin());

        Label showNameLabel = new Label("Benutzernamen dauerhaft anzeigen.", Chati.CHATI.getSkin());
        CheckBox showNameCheckBox = new CheckBox("", Chati.CHATI.getSkin());
        showNameCheckBox.setChecked(Chati.CHATI.getPreferences().getShowNamesInWorld());
        showNameCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enableButton();
                infoLabel.setText("Führe Welteinstellungen durch.");
            }
        });

        confirmButton = new ChatiTextButton("Übernehmen", false);
        disableButton();
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                disableButton();
                Chati.CHATI.getPreferences().setShowNamesInWorld(showNameCheckBox.isChecked());
                infoLabel.setText("Deine Änderungen wurden gespeichert!");
            }
        });

        ChatiTextButton defaultButton = new ChatiTextButton("Standardeinstellung", true);
        defaultButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                enableButton();
                showNameCheckBox.setChecked(ChatiPreferences.DEFAULT_SHOW_NAMES_IN_WORLD);
                infoLabel.setText("Führe Welteinstellungen durch.");
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("Abbrechen", true);
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
        showNameCheckBox.getImage().scaleBy(0.25f);
        container.add(showNameContainer).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(3).bottom().height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).padRight(SPACING / 2);
        buttonContainer.add(defaultButton).padLeft(SPACING / 2).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer);
        add(container).padLeft(SPACING).padRight(SPACING).grow();
    }

    private void enableButton() {
        confirmButton.setTouchable(Touchable.enabled);
        confirmButton.getLabel().setColor(Color.WHITE);
    }

    private void disableButton() {
        confirmButton.setTouchable(Touchable.disabled);
        confirmButton.getLabel().setColor(Color.DARK_GRAY);
    }
}
