package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.exception.UserNotFoundException;
import model.role.Role;
import model.user.AdministrativeAction;
import model.user.IUserView;
import view2.Assets;
import view2.Chati;
import view2.component.AbstractWindow;
import view2.component.ChatiTextArea;
import view2.component.ChatiTextField;

public class AdministratorManageWindow extends AbstractWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 450;
    private static final float ROW_HEIGHT = 60;
    private static final float SPACING = 15;

    private Label infoLabel;
    private ChatiTextField usernameField;
    private ChatiTextArea messageArea;
    private TextButton assignButton;
    private TextButton withdrawButton;
    private TextButton cancelButton;
    private TextButton closeButton;

    public AdministratorManageWindow() {
        super("Administratoren verwalten");
        create();
        setLayout();
    }

    @Override
    protected void create() {
        infoLabel = new Label("Erteile oder entziehe die Rolle des Administrators!", Assets.SKIN);

        usernameField = new ChatiTextField("Benutzername", false);
        messageArea = new ChatiTextArea("Füge eine Nachricht hinzu!", true);

        assignButton = new TextButton("Rolle erteilen", Assets.SKIN);
        assignButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (usernameField.getText().isBlank() || usernameField.getStyle().fontColor == Color.GRAY) {
                    infoLabel.setText("Bitte gib einen Benutzernamen ein!");
                    return;
                }
                if (usernameField.getText().equals(Chati.CHATI.getUserManager().getInternUserView().getUsername())) {
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
                if (!messageArea.getText().isBlank() && messageArea.getStyle().fontColor != Color.GRAY) {
                    message = messageArea.getText();
                }
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.USER_MANAGE, externUser.getUserId(),
                        AdministrativeAction.ASSIGN_ADMINISTRATOR, message);
                infoLabel.setText("Die Aktion war erfolgreich!");
                resetTextFields();
            }
        });

        withdrawButton = new TextButton("Rolle entziehen", Assets.SKIN);
        withdrawButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (usernameField.getText().isBlank() || usernameField.getStyle().fontColor == Color.GRAY) {
                    infoLabel.setText("Bitte gib einen Benutzernamen ein!");
                    return;
                }
                if (usernameField.getText().equals(Chati.CHATI.getUserManager().getInternUserView().getUsername())) {
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
                if (!messageArea.getText().isBlank() && messageArea.getStyle().fontColor != Color.GRAY) {
                    message = messageArea.getText();
                }
                Chati.CHATI.getServerSender().send(ServerSender.SendAction.USER_MANAGE, externUser.getUserId(),
                        AdministrativeAction.WITHDRAW_ADMINISTRATOR, message);
                infoLabel.setText("Die Aktion war erfolgreich!");
                resetTextFields();
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
        container.add(usernameField).row();
        container.add(messageArea).height(2 * ROW_HEIGHT).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).space(SPACING).growX();
        buttonContainer.add(assignButton, withdrawButton, cancelButton);
        container.add(buttonContainer);
        add(container).padLeft(SPACING).padRight(SPACING).grow();

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    private void resetTextFields() {
        usernameField.reset();
        messageArea.reset();
    }
}
