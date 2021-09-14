package view2.userInterface.menu.table;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
        infoLabel.setText("Bitte wähle eine Aktion aus!");

        ChatiTextButton changePasswordButton = new ChatiTextButton("Passwort ändern", true);
        changePasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new ChangePasswordTable());
            }
        });

        ChatiTextButton deleteAccountButton = new ChatiTextButton("Konto löschen", true);
        deleteAccountButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new DeleteAccountTable());
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("Zurück", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
    }

    @Override
    public void resetTextFields() {
    }
}
