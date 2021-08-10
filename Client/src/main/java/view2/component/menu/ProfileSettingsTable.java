package view2.component.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import view2.Chati;

public class ProfileSettingsTable extends MenuTable {

    private static final String NAME = "profile-settings-table";
    private TextButton changePasswordButton;
    private TextButton deleteAccountButton;
    private TextButton cancelButton;

    protected ProfileSettingsTable() {
        super(NAME);
    }

    @Override
    protected void create() {
        infoLabel.setText("Bitte wähle eine Aktion aus!");

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

        cancelButton = new TextButton("Abbrechen", SKIN);
        cancelButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getMenuScreen().setTable(new StartTable());
            }
        });
    }

    @Override
    protected void setLayout() {
        int width = 600;
        int height = 60;
        int verticalSpacing = 15;
        int horizontalSpacing = 15;

        Table infoContainer = new Table();
        infoContainer.add(infoLabel);
        add(infoContainer).width(width).height(height).center().spaceBottom(horizontalSpacing).row();

        Table buttonContainer = new Table();
        buttonContainer.setWidth(width);
        buttonContainer.defaults().space(verticalSpacing);
        add(buttonContainer).spaceBottom(horizontalSpacing).row();
        buttonContainer.add(changePasswordButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
        buttonContainer.add(deleteAccountButton).width(width / 2f - (verticalSpacing / 2f)).height(height);

        add(cancelButton).width(width).center().height(height);
    }

    @Override
    public void clearTextFields() {

    }
}
