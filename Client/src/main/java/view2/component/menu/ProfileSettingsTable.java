package view2.component.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import view2.Assets;

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
        Table infoContainer = new Table();
        infoContainer.add(infoLabel);
        add(infoContainer).width(ROW_WIDTH).height(ROW_HEIGHT).center().spaceBottom(HORIZONTAL_SPACING).row();

        Table buttonContainer = new Table();
        buttonContainer.setWidth(ROW_WIDTH);
        buttonContainer.defaults().space(VERTICAL_SPACING);
        add(buttonContainer).spaceBottom(HORIZONTAL_SPACING).row();
        buttonContainer.add(changePasswordButton).width(ROW_WIDTH / 2f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);
        buttonContainer.add(deleteAccountButton).width(ROW_WIDTH / 2f - (VERTICAL_SPACING / 2f)).height(ROW_HEIGHT);

        add(cancelButton).width(ROW_WIDTH).center().height(ROW_HEIGHT);
    }

    @Override
    public void resetTextFields() {

    }
}
