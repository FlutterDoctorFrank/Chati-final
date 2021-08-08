package view.UIComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.jetbrains.annotations.NotNull;

public class LoginTable extends UIComponentTable {
    private boolean waitingLoginResponse = false;
    private boolean waitingRegResponse = false;
    private TextField password;
    private TextField username;

    public LoginTable(Hud hud) {
        super(hud);
        setName("login-table");
        create();
    }

    private void create() {
        int spacing = 5;
        infoLabel = new Label("", skin);
        username = new TextField("Benutzername", skin);
        username.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                username.setText("");
            }
        });
        password = new TextField("Passwort", skin);
        password.setPasswordCharacter('*');
        password.setPasswordMode(true);
        password.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                password.setText("");
            }


        });
        TextButton login = new TextButton("Login", skin);
        login.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                /*
                if (!username.getText().matches("[A-Za-z0-9]{1,16}")) {
                    infoLabel.setText("Kein gültigen Name");
                } else if (!password.getText().matches("[A-Za-z0-9]{1,16}")) {
                    infoLabel.setText("Kein gültiges Passwort");
                } else {
                    hud.sendLoginRequest(username.getText(), password.getText());
                }
                 */
                hud.addMenuTable(new StartScreenTable(hud));
            }
        });

        TextButton register = new TextButton("Registrieren", skin);
        register.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
                    if (!username.getText().matches("[A-Za-z0-9]{1,16}")) {
                        infoLabel.setText("Kein gültigen Name");
                    } else if (!password.getText().matches("[A-Za-z0-9]{1,16}")) {
                        infoLabel.setText("Kein gültiges Passwort");
                    } else {
                        hud.sendRegistrationRequest(username.getText(), password.getText());
                    }
                }
            }
        });

        Table logRegButtons = new Table();
        logRegButtons.defaults().space(5);
        float logRegButtonsWidth = (login.getWidth() + register.getWidth() + spacing);

        add(infoLabel).width(logRegButtonsWidth);
        row();
        add(username).width(logRegButtonsWidth).height(25);
        row();
        add(password).width(logRegButtonsWidth).height(25);
        row();
        logRegButtons.add(login);
        logRegButtons.add(register);
        add(logRegButtons);
        row();

        TextButton exit = new TextButton("Beenden", skin);
        exit.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });
        add(exit).width(logRegButtonsWidth);
    }
}
