package view.UIComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import view.Screens.ScreenHandler;

public class LoginTable extends UIComponentTable {

    public LoginTable(Hud hud, ScreenHandler screenHandler) {
        super();
        create(hud, screenHandler);
    }

    private void create(Hud hud, ScreenHandler screenHandler) {
        int spacing = 5;
        Label infoLabel = new Label("", skin);
        TextField username = new TextField("Benutzername", skin);
        username.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                username.setText("");
            }
        });
        TextField password = new TextField("Passwort", skin);
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
                if (!username.getText().isEmpty() && !password.getText().isEmpty())
                    hud.addTable(new StartScreenTable(hud, screenHandler));
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
                    hud.addTable(new StartScreenTable(hud, screenHandler));
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
