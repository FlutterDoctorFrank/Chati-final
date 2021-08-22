package view2.component.menu.table;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import view2.Assets;
import view2.component.menu.MenuScreen;

public class ProfileSettingsTable extends MenuTable {

    private TextButton changePasswordButton;
    private TextButton deleteAccountButton;
    private TextButton cancelButton;

    @Override
    protected void create() {
        infoLabel.setText("Bitte wähle eine Aktion aus!");

        changePasswordButton = new TextButton("Passwort ändern", Assets.SKIN);
        changePasswordButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                MenuScreen.getInstance().setMenuTable(new ChangePasswordTable());
            }
        });

        deleteAccountButton = new TextButton("Konto löschen", Assets.SKIN);
        deleteAccountButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                MenuScreen.getInstance().setMenuTable(new DeleteAccountTable());
            }
        });

        cancelButton = new TextButton("Zurück", Assets.SKIN);
        cancelButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                MenuScreen.getInstance().setMenuTable(new StartTable());
            }
        });
    }

    @Override
    protected void setLayout() {
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().fillX().expandX();
        container.add(infoLabel).row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().height(ROW_HEIGHT).fillX().expandX();
        buttonContainer.add(changePasswordButton).spaceRight(SPACING);
        buttonContainer.add(deleteAccountButton);
        container.add(buttonContainer).row();
        container.add(cancelButton);
        add(container).width(ROW_WIDTH);
    }

    @Override
    public void resetTextFields() {

    }
}
