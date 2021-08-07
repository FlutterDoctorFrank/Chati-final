package view2.component.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import controller.network.ServerSender;
import view2.Chati;

public class StartTable extends MenuTable {

    private static final String NAME = "start-table";
    private SelectBox<ContextEntry> worldSelectBox;
    private TextButton enterWorldButton;
    private TextButton changeAvatarButton;
    private TextButton profileSettingsButton;

    public StartTable(MenuScreen menuScreen) {
        super(NAME, menuScreen);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        updateWorldList();
        super.draw(batch, parentAlpha);
    }

    @Override
    protected void create() {
        infoLabel = new Label("Bitte wählen Sie eine Welt aus.", SKIN);

        worldSelectBox = new SelectBox<>(SKIN);

        enterWorldButton = new TextButton("Welt Beitreten", SKIN);
        enterWorldButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getServerSender()
                    .send(ServerSender.SendAction.WORLD_ACTION, worldSelectBox.getSelected().getContextId(), true);
            }
        });

        changeAvatarButton = new TextButton("Avatar ändern", SKIN);
        changeAvatarButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                menuScreen.setTable(new AvatarSelectTable(menuScreen));
            }
        });

        profileSettingsButton = new TextButton("Konto verwalten.", SKIN);
        profileSettingsButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                menuScreen.setTable(new ProfileSettingsTable(menuScreen));
            }
        });
    }

    @Override
    protected void setLayout() {

    }

    private void updateWorldList() {
        worldSelectBox.setItems(menuScreen.getWorlds().toArray(new ContextEntry[0]));
    }
}
