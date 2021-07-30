package view.UIComponents;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import view.Screens.PlayScreen;


public class StartScreenTable extends UIComponentTable {

    public StartScreenTable(Hud hud) {
        super();
        this.hud = hud;
        create();
    }

    public void create() {
        int spacing = 5;

        add(dropDownMenus(hud)).right().padRight(10).padTop(10);
        row();

        Label infoLabel = new Label("", skin);
        TextButton play = new TextButton("Beitreten", skin);
        play.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hud.addTable(new PlayScreenTable(hud));
                hud.getApplicationScreen().getGame().setScreen(new PlayScreen(hud.getApplicationScreen().getGame()));
            }
        });

        SelectBox<String> worlds = new SelectBox<String>(skin);
        worlds.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                worlds.setSelected(worlds.getSelected());
            }
        });
        worlds.setItems("Welt auswählen", "a", "b", "c", "d");
        TextButton changeAvatar = new TextButton("Avatar ändern", skin);
        changeAvatar.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hud.addTable(new AvatarSelectTable(hud));
            }
        });
        TextButton logout = new TextButton("Abmelden", skin);
        TextButton profileSettings = new TextButton("Benutzerkonto \n bearbeiten", skin);
        profileSettings.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hud.addTable(new ProfileSettingsTable(hud));
            }
        });

        float rowHeight = profileSettings.getHeight();
        float rowWidth = logout.getWidth() + profileSettings.getWidth() + spacing;

        Table buttonGroup = new Table();
        buttonGroup.defaults().space(spacing).height(rowHeight);
        buttonGroup.add(logout, profileSettings);

        Table menuLayout = new Table();
        menuLayout.defaults().space(spacing).height(rowHeight).width(rowWidth);
        menuLayout.add(infoLabel);
        menuLayout.row();
        menuLayout.add(play);
        menuLayout.row();
        menuLayout.add(worlds);
        menuLayout.row();
        menuLayout.add(changeAvatar);
        menuLayout.row();
        menuLayout.add(buttonGroup);

        add(menuLayout).expand();
    }

}
