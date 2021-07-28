package view.UIComponents;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import view.Screens.ScreenHandler;


public class ProfileSettingsTable extends UIComponentTable {

    public ProfileSettingsTable(Hud hud, ScreenHandler screenHandler) {
        create(hud, screenHandler);
    }

    private void create(Hud hud, ScreenHandler screenHandler) {
        Label infoLabel = new Label("", skin);
        TextButton changePassword = new TextButton("Passwort ändern", skin);
        changePassword.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hud.addTable(new ChangePasswordTable(hud, screenHandler));
            }
        });

        TextButton deleteAccount = new TextButton("Konto löschen", skin);
        deleteAccount.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hud.addTable(new DeleteAccountTable(hud, screenHandler));
            }
        });

        TextButton back = new TextButton("Zurück", skin);
        back.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hud.addTable(new StartScreenTable(hud, screenHandler));
            }
        });



        add(infoLabel);
        row();
        add(changePassword);
        row();
        add(deleteAccount);
        row();
        add(back);
    }
}
