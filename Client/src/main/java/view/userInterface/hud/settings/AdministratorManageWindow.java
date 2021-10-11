package view.userInterface.hud.settings;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.exception.UserNotFoundException;
import model.role.Role;
import model.user.AdministrativeAction;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import view.Chati;
import view.userInterface.ChatiLabel;
import view.userInterface.ChatiTextArea;
import view.userInterface.ChatiTextButton;
import view.userInterface.ChatiTextField;
import view.userInterface.ChatiWindow;

/**
 * Eine Klasse, welche das Menü zum Vergeben oder Entziehen der Administratorrolle repräsentiert.
 */
public class AdministratorManageWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 450;
    private static final float ROW_HEIGHT = 60;
    private static final float SPACING = 15;

    private final ChatiTextField usernameField;
    private final ChatiTextArea messageArea;

    /**
     * Erzeugt eine neue Instanz des AdministratorManageWindow.
     */
    public AdministratorManageWindow() {
        super("window.title.manage-administrator", WINDOW_WIDTH, WINDOW_HEIGHT);

        infoLabel = new ChatiLabel("window.entry.manage-administrator");
        usernameField = new ChatiTextField("menu.text-field.username", ChatiTextField.TextFieldType.STANDARD);
        messageArea = new ChatiTextArea("menu.text-field.message", true);

        ChatiTextButton assignButton = new ChatiTextButton("menu.button.role-add", true);
        assignButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (usernameField.isBlank()) {
                    showMessage("window.general.no-username");
                    return;
                }
                if (Chati.CHATI.getInternUser() != null
                        && usernameField.getText().equals(Chati.CHATI.getInternUser().getUsername())) {
                    showMessage("window.administrator.assign-self");
                    resetTextFields();
                    return;
                }
                IUserView externUser;
                try {
                    externUser = Chati.CHATI.getUserManager().getExternUserView(usernameField.getText());
                } catch (UserNotFoundException e) {
                    showMessage("window.general.user-not-found");
                    resetTextFields();
                    return;
                }
                if (externUser.hasRole(Role.ADMINISTRATOR) || externUser.hasRole(Role.OWNER)) {
                    showMessage("window.administrator.assign-already");
                    resetTextFields();
                    return;
                }
                String message = "";
                if (!messageArea.isBlank()) {
                    message = messageArea.getText();
                }
                Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, externUser.getUserId(),
                        AdministrativeAction.ASSIGN_ADMINISTRATOR, message);
                showMessage("window.general.success");
                resetTextFields();
            }
        });

        ChatiTextButton withdrawButton = new ChatiTextButton("menu.button.role-remove", true);
        withdrawButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (usernameField.isBlank()) {
                    showMessage("window.general.no-username");
                    return;
                }
                if (Chati.CHATI.getInternUser() != null
                        && usernameField.getText().equals(Chati.CHATI.getInternUser().getUsername())) {
                    showMessage("window.administrator.withdraw-self");
                    resetTextFields();
                    return;
                }
                IUserView externUser;
                try {
                    externUser = Chati.CHATI.getUserManager().getExternUserView(usernameField.getText());
                } catch (UserNotFoundException e) {
                    showMessage("window.general.user-not-found");
                    resetTextFields();
                    return;
                }
                if (!externUser.hasRole(Role.ADMINISTRATOR) && !externUser.hasRole(Role.OWNER)) {
                    showMessage("window.administrator.withdraw-already");
                    resetTextFields();
                    return;
                }
                if (externUser.hasRole(Role.OWNER)) {
                    showMessage("window.administrator.withdraw-denied");
                    resetTextFields();
                    return;
                }
                String message = "";
                if (!messageArea.isBlank()) {
                    message = messageArea.getText();
                }
                Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, externUser.getUserId(),
                        AdministrativeAction.WITHDRAW_ADMINISTRATOR, message);
                showMessage("window.general.success");
                resetTextFields();
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("menu.button.cancel", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                close();
            }
        });

        // Layout
        setModal(true);
        setMovable(false);

        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        infoLabel.setAlignment(Align.center, Align.center);
        container.add(infoLabel).row();
        container.add(usernameField).row();
        container.add(messageArea).height(2 * ROW_HEIGHT).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        buttonContainer.add(assignButton).padRight(SPACING / 2);
        buttonContainer.add(withdrawButton).padLeft(SPACING / 2).padRight(SPACING / 2);
        buttonContainer.add(cancelButton).padLeft(SPACING / 2);
        container.add(buttonContainer);
        add(container).padLeft(SPACING).padRight(SPACING).grow();

        // Translatable register
        translatables.add(infoLabel);
        translatables.add(usernameField);
        translatables.add(messageArea);
        translatables.add(assignButton);
        translatables.add(withdrawButton);
        translatables.add(cancelButton);
        translatables.trimToSize();
    }

    /**
     * Setzt alle Textfelder zurück.
     */
    private void resetTextFields() {
        usernameField.reset();
        messageArea.reset();
    }

    @Override
    public void focus() {
    }
}
