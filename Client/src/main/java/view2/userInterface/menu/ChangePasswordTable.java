package view2.userInterface.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiTable;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTextField;
import view2.Response;

/**
 * Eine Klasse, welche das Menü zum Ändern des Passworts repräsentiert.
 */
public class ChangePasswordTable extends ChatiTable {

    private final ChatiTextField passwordField;
    private final ChatiTextField newPasswordField;
    private final ChatiTextField confirmNewPasswordField;

    /**
     * Erzeugt eine neue Instanz des ChangePasswordTable.
     */
    public ChangePasswordTable() {
        super("table.entry.change-password");

        passwordField = new ChatiTextField("menu.text-field.password-current", ChatiTextField.TextFieldType.PASSWORD);
        newPasswordField = new ChatiTextField("menu.text-field.password-new", ChatiTextField.TextFieldType.PASSWORD);
        confirmNewPasswordField = new ChatiTextField("menu.text-field.password-new-confirm", ChatiTextField.TextFieldType.PASSWORD);

        ChatiTextButton confirmButton = new ChatiTextButton("menu.button.confirm", true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (passwordField.isBlank() || newPasswordField.isBlank()
                        || confirmNewPasswordField.isBlank()) {
                    showMessage("table.general.fill-fields");
                    return;
                }
                if (!newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
                    showMessage("table.change-password.no-match");
                    newPasswordField.reset();
                    confirmNewPasswordField.reset();
                    return;
                }
                if (passwordField.getText().equals(newPasswordField.getText())) {
                    showMessage("table.change-password.no-new");
                    newPasswordField.reset();
                    confirmNewPasswordField.reset();
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.PASSWORD_CHANGE);
                Chati.CHATI.send(ServerSender.SendAction.PROFILE_CHANGE, passwordField.getText(), newPasswordField.getText());
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("menu.button.back", true);
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
        container.add(newPasswordField).row();
        container.add(confirmNewPasswordField).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(confirmButton).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer);
        add(container).width(ROW_WIDTH);

        // Translatable register
        translatables.add(passwordField);
        translatables.add(newPasswordField);
        translatables.add(confirmNewPasswordField);
        translatables.add(confirmButton);
        translatables.add(cancelButton);
        translatables.trimToSize();
    }

    @Override
    public void resetTextFields() {
        passwordField.reset();
        newPasswordField.reset();
        confirmNewPasswordField.reset();
    }
}
