package view2.component.menu.table;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import view2.Assets;
import view2.Chati;
import view2.component.menu.MenuScreen;

public class AvatarSelectTable extends MenuTable {

    private TextButton confirmButton;
    private TextButton cancelButton;

    @Override
    protected void create() {
        infoLabel.setText("Bitte wähle einen Avatar!");

        confirmButton = new TextButton("Bestätigen", Assets.SKIN);
        confirmButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.CHATI.getMenuScreen().setMenuTable(new StartTable());
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
                Chati.CHATI.getMenuScreen().setMenuTable(new StartTable());
            }
        });
    }

    @Override
    protected void setLayout() {
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        container.add(infoLabel).row();
        // avatar scrollpane kommt hier rein
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).spaceRight(SPACING).growX();
        buttonContainer.add(confirmButton, cancelButton);
        container.add(buttonContainer);
        add(container).width(ROW_WIDTH);
    }

    @Override
    public void resetTextFields() {

    }
}
