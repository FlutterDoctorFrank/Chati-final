package view2.component.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
        infoLabel = new Label("Wähle einen Avatar.", SKIN);

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

    }
}
