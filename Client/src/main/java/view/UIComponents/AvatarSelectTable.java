package view.UIComponents;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import view.Screens.ScreenHandler;

import java.awt.*;


public class AvatarSelectTable extends UIComponentTable {

    public AvatarSelectTable(Hud hud, ScreenHandler screenHandler) {
        create(hud, screenHandler);
    }

    private void create(Hud hud, ScreenHandler screenHandler) {
        int spacing = 5;

        Label infoLabel = new Label("", skin);
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
                hud.addTable(new StartScreenTable(hud, screenHandler));
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
                hud.addTable(new StartScreenTable(hud, screenHandler));
            }
        });
        Table buttons = new Table();
        buttons.defaults().space(spacing);
        buttons.add(confirm, abort);

        add(dropDownMenus(hud)).right().padRight(10).padTop(10);
        row();
        add(infoLabel);
        row();
        add(avatars).expand();
        row();
        add(buttons).padBottom(50);

    }
}
