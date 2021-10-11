package view.userInterface.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import org.jetbrains.annotations.NotNull;
import view.Chati;
import view.userInterface.ChatiTable;
import view.userInterface.ChatiTextButton;
import view.userInterface.ChatiTextField;
import view.Response;

/**
 * Eine Klasse, welche das Menü zum Löschen eines Kontos repräsentiert.
 */
public class DeleteAccountTable extends ChatiTable {

    private final ChatiTextField passwordField;
    private final ChatiTextField confirmPasswordField;

    /**
     * Erzeugt eine neue Instanz des DeleteAccountTable.
     */
    public DeleteAccountTable() {
        super("table.entry.delete-account");

        passwordField = new ChatiTextField("menu.text-field.password", ChatiTextField.TextFieldType.PASSWORD);
        confirmPasswordField = new ChatiTextField("menu.text-field.password-confirm", ChatiTextField.TextFieldType.PASSWORD);

        ChatiTextButton confirmButton = new ChatiTextButton("menu.button.confirm", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (passwordField.isBlank() || confirmPasswordField.isBlank()) {
                    showMessage("table.general.fill-fields");
                    return;
                }
                if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                    showMessage("table.delete-account.no-match");
                    resetTextFields();
                    return;
                }
                Chati.CHATI.getMenuScreen().setMenuTable(new ConfirmDeletionTable());
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("menu.button.cancel", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new ProfileSettingsTable());
            }
        });

        // Layout
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        container.add(infoLabel).row();
        container.add(passwordField).row();
        container.add(confirmPasswordField).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer);
        add(container).width(ROW_WIDTH);

        // Translatable register
        translatables.add(passwordField);
        translatables.add(confirmPasswordField);
        translatables.add(confirmButton);
        translatables.add(cancelButton);
        translatables.trimToSize();
    }

    @Override
    public void resetTextFields() {
        passwordField.reset();
        confirmPasswordField.reset();
    }

    /**
     * Eine Klasse, welche das Menü zum Bestätigen der Kontolöschung repräsentiert.
     */
    private class ConfirmDeletionTable extends ChatiTable {

        /**
         * Erzeugt eine neue Instanz des ConfirmDeletionTable.
         */
        public ConfirmDeletionTable() {
            super("table.delete-account.confirm");

            ChatiTextButton confirmButton = new ChatiTextButton("menu.button.confirm", true);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    Chati.CHATI.getMenuScreen().setPendingResponse(Response.DELETE_ACCOUNT);
                    Chati.CHATI.send(ServerSender.SendAction.PROFILE_LOGOUT, passwordField.getText(), true);
                }
            });

            ChatiTextButton cancelButton = new ChatiTextButton("menu.button.back", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    Chati.CHATI.getMenuScreen().setMenuTable(new DeleteAccountTable());
                }
            });

            // Layout
            Table container = new Table();
            container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
            container.add(infoLabel).row();
            Table buttonContainer = new Table();
            buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
            buttonContainer.add(confirmButton).padRight(SPACING / 2);
            buttonContainer.add(cancelButton).padLeft(SPACING / 2);
            container.add(buttonContainer);
            add(container).width(ROW_WIDTH);

            // Translatable register
            translatables.add(confirmButton);
            translatables.add(cancelButton);
            translatables.trimToSize();
        }

        @Override
        public void resetTextFields() {
        }
    }
}
