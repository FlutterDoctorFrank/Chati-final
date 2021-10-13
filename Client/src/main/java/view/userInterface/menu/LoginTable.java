package view.userInterface.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import org.jetbrains.annotations.NotNull;
import view.Chati;
import view.userInterface.actor.ChatiTable;
import view.userInterface.actor.ChatiTextButton;
import view.userInterface.actor.ChatiTextField;
import view.Response;

/**
 * Eine Klasse, welche das Menü zum Anmelden repräsentiert.
 */
public class LoginTable extends ChatiTable {

    private final ChatiTextField usernameField;
    private final ChatiTextField passwordField;

    /**
     * Erzeugt eine neue Instanz des LoginTable.
     */
    public LoginTable() {
        super("table.entry.login");

        usernameField = new ChatiTextField("menu.text-field.username", ChatiTextField.TextFieldType.STANDARD);
        passwordField = new ChatiTextField("menu.text-field.password", ChatiTextField.TextFieldType.PASSWORD);

        ChatiTextButton loginButton = new ChatiTextButton("menu.button.login", true);
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (usernameField.isBlank() || passwordField.isBlank()) {
                    showMessage("table.general.fill-fields");
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.LOGIN);
                Chati.CHATI.send(ServerSender.SendAction.PROFILE_LOGIN, usernameField.getText(), passwordField.getText(), false);
            }
        });

        ChatiTextButton registerButton = new ChatiTextButton("menu.button.register", true);
        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (usernameField.isBlank() || passwordField.isBlank()) {
                    showMessage("table.general.fill-fields");
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.REGISTRATION);
                Chati.CHATI.send(ServerSender.SendAction.PROFILE_LOGIN, usernameField.getText(), passwordField.getText(), true);
            }
        });

        ChatiTextButton exitButton = new ChatiTextButton("menu.button.exit", true);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Gdx.app.exit();
            }
        });

        // Layout
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        container.add(infoLabel).row();
        container.add(usernameField).row();
        container.add(passwordField).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(loginButton).padRight(SPACING / 2);
        buttonContainer.add(registerButton).padLeft(SPACING / 2);
        container.add(buttonContainer).row();
        container.add(exitButton);
        add(container).width(ROW_WIDTH);

        // Translatable register
        translatables.add(usernameField);
        translatables.add(passwordField);
        translatables.add(loginButton);
        translatables.add(registerButton);
        translatables.add(exitButton);
        translatables.trimToSize();
    }

    @Override
    public void resetTextFields() {
        usernameField.reset();
        passwordField.reset();
    }
}