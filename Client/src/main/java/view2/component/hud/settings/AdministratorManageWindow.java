package view2.component.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import controller.network.ServerSender;
import model.exception.UserNotFoundException;
import model.role.Role;
import model.user.AdministrativeAction;
import model.user.IUserView;
import view2.Assets;
import view2.Chati;
import view2.component.ChatiWindow;

public class AdministratorManageWindow extends ChatiWindow {

    private static final float WINDOW_WIDTH = 750;
    private static final float WINDOW_HEIGHT = 500;
    private static final float ROW_WIDTH = 650;
    private static final float ROW_HEIGHT = 60;
    private static final float VERTICAL_SPACING = 15;
    private static final float HORIZONTAL_SPACING = 15;
    private static final float TEXTFIELD_FONT_SCALE_FACTOR = 1.6f;

    private Label infoLabel;
    private TextField usernameField;
    private TextArea messageArea;
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

        usernameField = new TextField("Benutzername", Assets.getNewSkin());
        usernameField.getStyle().fontColor = Color.GRAY;
        usernameField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if (focused && usernameField.getStyle().fontColor == Color.GRAY) {
                    usernameField.setText("");
                    usernameField.getStyle().fontColor = Color.BLACK;
                }
                else if (usernameField.getText().isEmpty()) {
                    resetUsernameField();
                }
            }
        });

        messageArea = new TextArea("Füge eine Nachricht hinzu!", Assets.getNewSkin());
        messageArea.getStyle().fontColor = Color.GRAY;
        messageArea.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if (focused && messageArea.getStyle().fontColor == Color.GRAY) {
                    messageArea.setText("");
                    messageArea.getStyle().fontColor = Color.BLACK;
                }
                else if (messageArea.getText().isEmpty()) {
                    resetMessageArea();
                }
            }
        });

        assignButton = new TextButton("Rolle erteilen", Assets.SKIN);
        assignButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (usernameField.getText().isEmpty() || usernameField.getStyle().fontColor == Color.GRAY) {
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
                if (!messageArea.getText().isEmpty() && messageArea.getStyle().fontColor != Color.GRAY) {
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
                if (usernameField.getText().isEmpty() || usernameField.getStyle().fontColor == Color.GRAY) {
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
                if (!messageArea.getText().isEmpty() && messageArea.getStyle().fontColor != Color.GRAY) {
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
                remove();
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
                remove();
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

        Table labelContainer = new Table();
        labelContainer.add(infoLabel).center();
        add(labelContainer).width(ROW_WIDTH).height(ROW_HEIGHT).spaceTop(VERTICAL_SPACING).spaceBottom(VERTICAL_SPACING).row();

        add(usernameField).width(ROW_WIDTH).height(ROW_HEIGHT).spaceBottom(VERTICAL_SPACING).row();
        add(messageArea).width(ROW_WIDTH).height(2 * ROW_HEIGHT).spaceBottom(VERTICAL_SPACING).row();

        Table buttonContainer = new Table();
        buttonContainer.defaults().width((ROW_WIDTH - VERTICAL_SPACING) / 3).height(ROW_HEIGHT).space(HORIZONTAL_SPACING);
        buttonContainer.add(assignButton, withdrawButton, cancelButton);
        add(buttonContainer).width(ROW_WIDTH).height(ROW_HEIGHT);

        getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
    }

    private void resetTextFields() {
        resetUsernameField();
        resetMessageArea();
    }

    private void resetUsernameField() {
        usernameField.getStyle().fontColor = Color.GRAY;
        usernameField.setText("Benutzername");
        getStage().unfocus(usernameField);
    }

    private void resetMessageArea() {
        messageArea.getStyle().fontColor = Color.GRAY;
        messageArea.setText("Füge eine Nachricht hinzu!");
        getStage().unfocus(messageArea);
    }
}
