package view.UIComponents;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class StartScreenTable extends UIComponentTable {

    public StartScreenTable(Hud hud) {
        super(hud);
        setName("start-screen-table");
        create();
    }

    public void create() {
        int spacing = 5;
        infoLabel = new Label("", skin);

        SelectBox<String> worldSelect = new SelectBox<>(skin);
        updateWorldsList(worldSelect);

        worldSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                worldSelect.setSelected(worldSelect.getSelected());
            }
        });

        TextButton play = new TextButton("Beitreten", skin);
        play.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                for (var entry : hud.getWorlds().entrySet()) {
                    if (entry.getValue().equals(worldSelect.getSelected())) {
                        hud.sendJoinWorldRequest(entry.getKey());
                        break;
                    }
                    infoLabel.setText("Welt nicht mehr verfügbar");
                    updateWorldsList(worldSelect);
                }
                hud.addMenuTable(null);
            }
        });

        TextButton changeAvatar = new TextButton("Avatar ändern", skin);
        changeAvatar.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hud.addMenuTable(new AvatarSelectTable(hud));
            }
        });
        TextButton logout = new TextButton("Abmelden", skin);
        logout.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hud.addMenuTable(new LoginTable(hud));
            }
        });

        TextButton profileSettings = new TextButton("Benutzerkonto \n bearbeiten", skin);
        profileSettings.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hud.addMenuTable(new ProfileSettingsTable(hud));
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
        menuLayout.add(worldSelect);
        menuLayout.row();
        menuLayout.add(changeAvatar);
        menuLayout.row();
        menuLayout.add(buttonGroup);

        add(menuLayout).expand();
    }


    private void updateWorldsList(SelectBox<String> selectBox) {
        for (var entry : hud.getWorlds().entrySet()) {
            selectBox.setItems(entry.getValue());
        }
    }
}