package view2.component.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import view2.Chati;

public class AvatarSelectTable extends MenuTable {

    private static final String NAME = "avatar-select-table";
    private TextButton confirmButton;
    private TextButton cancelButton;

    protected AvatarSelectTable() {
        super(NAME);
    }

    @Override
    protected void create() {
        infoLabel.setText("Bitte wähle einen Avatar!");

        confirmButton = new TextButton("Bestätigen", SKIN);
        confirmButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getMenuScreen().setTable(new StartTable());
            }
        });

        cancelButton = new TextButton("Zurück", SKIN);
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
        add(buttonContainer).spaceBottom(horizontalSpacing);
        buttonContainer.add(confirmButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
        buttonContainer.add(cancelButton).width(width / 2f - (verticalSpacing / 2f)).height(height);
    }

    @Override
    public void clearTextFields() {

    }
}
