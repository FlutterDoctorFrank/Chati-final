package view2.userInterface.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.ChatiPreferences;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiWindow;

/**
 * Eine Klasse, welche das Menü für diverse Einstellungen innerhalb einer Welt repräsentiert.
 */
public class WorldConfigurationWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 400;

    private final ChatiTextButton confirmButton;

    /**
     * Erzeugt eine neue Instanz des WorldConfigurationWindow.
     */
    public WorldConfigurationWindow() {
        super("window.title.world-settings");

        infoLabel = new ChatiLabel("window.entry.world-settings");

        ChatiLabel alwaysSprintLabel = new ChatiLabel("menu.label.sprint-always");
        CheckBox alwaysSprintCheckBox = new CheckBox("", Chati.CHATI.getSkin());
        alwaysSprintCheckBox.setChecked(Chati.CHATI.getPreferences().isAlwaysSprinting());
        alwaysSprintCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(@NotNull final ChangeEvent event, @NotNull final Actor actor) {
                enableButton();
                infoLabel.translate();
            }
        });

        ChatiLabel showNameLabel = new ChatiLabel("menu.label.show-usernames");
        CheckBox showNameCheckBox = new CheckBox("", Chati.CHATI.getSkin());
        showNameCheckBox.setChecked(Chati.CHATI.getPreferences().getShowNamesInWorld());
        showNameCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(@NotNull final ChangeEvent event, @NotNull final Actor actor) {
                enableButton();
                infoLabel.translate();
            }
        });

        confirmButton = new ChatiTextButton("menu.button.apply", true);
        disableButton();
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                disableButton();
                Chati.CHATI.getPreferences().setAlwaysSprinting(alwaysSprintCheckBox.isChecked());
                Chati.CHATI.getPreferences().setShowNamesInWorld(showNameCheckBox.isChecked());
                showMessage("window.settings.saved");
            }
        });

        ChatiTextButton defaultButton = new ChatiTextButton("menu.button.default-settings", true);
        defaultButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                alwaysSprintCheckBox.setChecked(ChatiPreferences.DEFAULT_ALWAYS_SPRINTING);
                showNameCheckBox.setChecked(ChatiPreferences.DEFAULT_SHOW_NAMES_IN_WORLD);
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("menu.button.cancel", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                close();
            }
        });

        setModal(true);
        setMovable(false);
        setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
        setWidth(WINDOW_WIDTH);
        setHeight(WINDOW_HEIGHT);

        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING / 2).center().growX();
        infoLabel.setAlignment(Align.center, Align.center);
        container.add(infoLabel).row();
        Table alwaysSprintTable = new Table();
        alwaysSprintTable.defaults().colspan(16).height(ROW_HEIGHT).space(SPACING).growX();
        alwaysSprintTable.add(alwaysSprintLabel).colspan(15);
        alwaysSprintTable.add(alwaysSprintCheckBox).colspan(1);
        alwaysSprintCheckBox.getImage().scaleBy(0.25f);
        container.add(alwaysSprintTable).row();
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
        container.add(buttonContainer).padTop(SPACING);
        add(container).padLeft(SPACING).padRight(SPACING).grow();

        // Translatable register
        translates.add(infoLabel);
        translates.add(alwaysSprintLabel);
        translates.add(showNameLabel);
        translates.add(confirmButton);
        translates.add(defaultButton);
        translates.add(cancelButton);
        translates.trimToSize();
    }

    /**
     * Aktiviert den Bestätigungsbutton.
     */
    private void enableButton() {
        confirmButton.setTouchable(Touchable.enabled);
        confirmButton.getLabel().setColor(Color.WHITE);
    }

    /**
     * Deaktiviert den Bestätigungsbutton.
     */
    private void disableButton() {
        confirmButton.setTouchable(Touchable.disabled);
        confirmButton.getLabel().setColor(Color.DARK_GRAY);
    }
}
