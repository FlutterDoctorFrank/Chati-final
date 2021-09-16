package view2.userInterface.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiTextButton;

/**
 * Eine Klasse, welche das Menü zum Vornehmen von Einstellungen am Benutzerkonto repräsentiert.
 */
public class ProfileSettingsTable extends MenuTable {

    /**
     * Erzeugt eine neue Instanz des ProfileSettingsTable.
     */
    public ProfileSettingsTable() {
        super("table.entry.choose-action");

        ChatiTextButton changePasswordButton = new ChatiTextButton("menu.button.change-password", true);
        changePasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new ChangePasswordTable());
            }
        });

        ChatiTextButton deleteAccountButton = new ChatiTextButton("menu.button.delete-account", true);
        deleteAccountButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new DeleteAccountTable());
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("menu.button.back", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new StartTable());
            }
        });

        // Layout
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        container.add(infoLabel).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(changePasswordButton).padRight(SPACING / 2);
        buttonContainer.add(deleteAccountButton).padLeft(SPACING / 2);
        container.add(buttonContainer).row();
        container.add(cancelButton);
        add(container).width(ROW_WIDTH);

        // Translatable register
        translates.add(changePasswordButton);
        translates.add(deleteAccountButton);
        translates.add(cancelButton);
        translates.trimToSize();
    }

    @Override
    public void resetTextFields() {
    }
}
