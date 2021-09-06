package view2.userInterface.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.exception.UserNotFoundException;
import model.role.Role;
import model.user.AdministrativeAction;
import model.user.IUserView;
import view2.Chati;
import view2.userInterface.ChatiWindow;
import view2.userInterface.ChatiTextArea;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTextField;

public class AdministratorManageWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 450;
    private static final float ROW_HEIGHT = 60;
    private static final float SPACING = 15;

    private final ChatiTextField usernameField;
    private final ChatiTextArea messageArea;

    public AdministratorManageWindow() {
        super("Administratoren verwalten");

        Label infoLabel = new Label("Erteile oder entziehe die Rolle des Administrators!", Chati.CHATI.getSkin());

        usernameField = new ChatiTextField("Benutzername", false);
        messageArea = new ChatiTextArea("Füge eine Nachricht hinzu!", true);

        ChatiTextButton assignButton = new ChatiTextButton("Rolle erteilen", true);
        assignButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (usernameField.isBlank()) {
                    infoLabel.setText("Bitte gib einen Benutzernamen ein!");
                    return;
                }
                if (usernameField.getText().equals(Chati.CHATI.getInternUser().getUsername())) {
                    infoLabel.setText("Du kannst dir nicht selbst eine Rolle erteilen!");
                    resetTextFields();
                    return;
                }
                IUserView externUser;
                try {
                    externUser = Chati.CHATI.getUserManager().getExternUserView(usernameField.getText());
                } catch (UserNotFoundException e) {
                    infoLabel.setText("Es existiert kein Benutzer mit diesem Namen.");
                    resetTextFields();
                    return;
                }
                if (externUser.hasRole(Role.ADMINISTRATOR) || externUser.hasRole(Role.OWNER)) {
                    infoLabel.setText("Dieser Benutzer hat bereits Administratorenrechte.");
                    resetTextFields();
                    return;
                }
                String message = "";
                if (!messageArea.isBlank()) {
                    message = messageArea.getText();
                }
                Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, externUser.getUserId(),
                        AdministrativeAction.ASSIGN_ADMINISTRATOR, message);
                infoLabel.setText("Die Aktion war erfolgreich!");
                resetTextFields();
            }
        });

        ChatiTextButton withdrawButton = new ChatiTextButton("Rolle entziehen", true);
        withdrawButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (usernameField.isBlank()) {
                    infoLabel.setText("Bitte gib einen Benutzernamen ein!");
                    return;
                }
                if (usernameField.getText().equals(Chati.CHATI.getInternUser().getUsername())) {
                    infoLabel.setText("Du kannst dir nicht selbst eine Rolle entziehen!");
                    resetTextFields();
                    return;
                }
                IUserView externUser;
                try {
                    externUser = Chati.CHATI.getUserManager().getExternUserView(usernameField.getText());
                } catch (UserNotFoundException e) {
                    infoLabel.setText("Es existiert kein Benutzer mit diesem Namen.");
                    resetTextFields();
                    return;
                }
                if (!externUser.hasRole(Role.ADMINISTRATOR) && !externUser.hasRole(Role.OWNER)) {
                    infoLabel.setText("Dieser Benutzer hat keine Administratorenrechte.");
                    resetTextFields();
                    return;
                }
                if (externUser.hasRole(Role.OWNER)) {
                    infoLabel.setText("Du kannst einem anderen Besitzer nicht die Rechte wegnehmen.");
                    resetTextFields();
                    return;
                }
                String message = "";
                if (!messageArea.isBlank()) {
                    message = messageArea.getText();
                }
                Chati.CHATI.send(ServerSender.SendAction.USER_MANAGE, externUser.getUserId(),
                        AdministrativeAction.WITHDRAW_ADMINISTRATOR, message);
                infoLabel.setText("Die Aktion war erfolgreich!");
                resetTextFields();
            }
        });

        ChatiTextButton cancelButton = new ChatiTextButton("Abbrechen", true);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });

        // Layout
        setModal(true);
        setMovable(false);
        setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
        setWidth(WINDOW_WIDTH);
        setHeight(WINDOW_HEIGHT);

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
    }

    private void resetTextFields() {
        usernameField.reset();
        messageArea.reset();
    }
}
