package view.UIComponents;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.context.Context;
import model.context.spatial.SpatialMap;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionListener;
import java.util.EnumSet;

public class StartScreenTable extends MenuTable {

    private SelectBox<String> worldSelect;
    private TextField newWorldName;
    private SelectBox<String> mapSelect;

    public StartScreenTable(Hud hud) {
        super(hud);
        setName("start-screen-table");
        create();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        updateMapList(mapSelect);
        updateWorldsList(worldSelect);
        super.draw(batch, parentAlpha);
    }

    public void create() {
        int spacing = 5;
        infoLabel = new Label("", skin);

        //select world drop down menu
        worldSelect = new SelectBox<>(skin);
        updateWorldsList(worldSelect);
        worldSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                worldSelect.setSelected(worldSelect.getSelected());
            }
        });

        //play button
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
                    } else {
                        infoLabel.setText("Welt nicht mehr verfügbar");
                        updateWorldsList(worldSelect);
                    }
                }
               // hud.addMenuTable(null);
            }
        });

        //change avatar button
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

        //profile settings button
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

        //textfield for the name of the new world
        newWorldName = new TextField("Welt Name", skin);
        newWorldName.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                newWorldName.setText("");
            }
        });

        //select map for the new world
        mapSelect = new SelectBox<String>(skin);
        updateMapList(mapSelect);
        mapSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                mapSelect.setSelected(mapSelect.getSelected());
            }
        });

        TextButton newWorldCreate = new TextButton("Neue Welt \n erstellen", skin);
        newWorldCreate.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(newWorldName.getText().isEmpty()) {
                    displayMessage("Welt Name kann nicht leer sein");
                } else if (mapSelect.getSelected().isEmpty()) {
                    displayMessage("Wehlen Sie eine Karte");
                } else {
                    for (var entry : EnumSet.allOf(SpatialMap.class)) {
                        if (entry.getName().equals(mapSelect.getSelected())) {
                            hud.sendCreateWorldRequest(entry, newWorldName.getText());
                        }
                    }
                }
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
        menuLayout.add(newWorldName);
        menuLayout.row();
        menuLayout.add(mapSelect);
        menuLayout.row();
        menuLayout.add(newWorldCreate);
        menuLayout.row();
        menuLayout.add(changeAvatar);
        menuLayout.row();
        menuLayout.add(buttonGroup);

        add(menuLayout).expand();
    }


    private void updateMapList(SelectBox<String> selectBox) {
       for (var entry : EnumSet.allOf(SpatialMap.class)) {
           selectBox.setItems(entry.getName());
        }
    }

    private void updateWorldsList(SelectBox<String> selectBox) {
        for (var entry : hud.getWorlds().entrySet()) {
            selectBox.setItems(entry.getValue());
        }
    }

}