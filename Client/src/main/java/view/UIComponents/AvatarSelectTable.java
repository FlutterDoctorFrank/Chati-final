package view.UIComponents;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;


public class AvatarSelectTable extends MenuTable {

    public AvatarSelectTable(Hud hud) {
        super(hud);
        setName("avatar-select-table");
        create();
    }

    private void create() {
        int spacing = 5;

        infoLabel = new Label("", skin);
        Table avatars = new Table();
        avatars.defaults().space(5);
        TextButton confirm = new TextButton("Bestätigen", skin);
        confirm.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hud.addMenuTable(new StartScreenTable(hud));
            }
        });
        TextButton abort = new TextButton("Abbrechen", skin);
        abort.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hud.addMenuTable(new StartScreenTable(hud));
            }
        });
        Table buttons = new Table();
        buttons.defaults().space(spacing);
        buttons.add(confirm, abort);

        add(infoLabel);
        row();
        add(avatars).expand();
        row();
        add(buttons).padBottom(50);

    }
}
