package view2.component.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import view2.Chati;

public class ProfileSettingsTable extends MenuTable {

    private static final String NAME = "profile-settings-table";
    private TextButton changePasswordButton;
    private TextButton deleteAccountButton;

    protected ProfileSettingsTable() {
        super(NAME);
    }

    @Override
    protected void create() {
        infoLabel = new Label("Wähle eine Aktion", SKIN);

        changePasswordButton = new TextButton("Passwort ändern", SKIN);
        changePasswordButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getMenuScreen().setTable(new ChangePasswordTable());
            }
        });

        deleteAccountButton = new TextButton("Konto löschen", SKIN);
        deleteAccountButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getMenuScreen().setTable(new DeleteAccountTable());
            }
        });
    }

    @Override
    protected void setLayout() {

    }
}
