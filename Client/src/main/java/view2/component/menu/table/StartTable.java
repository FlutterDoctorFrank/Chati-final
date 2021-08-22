package view2.component.menu.table;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import view2.Assets;
import view2.Chati;
import view2.component.menu.ContextEntry;
import view2.component.menu.MenuResponse;
import view2.component.menu.MenuScreen;

public class StartTable extends MenuTable {

    private Label worldSelectLabel;
    private SelectBox<ContextEntry> worldSelectBox;
    private TextButton joinWorldButton;
    private TextButton createWorldButton;
    private TextButton deleteWorldButton;
    private TextButton changeAvatarButton;
    private TextButton profileSettingsButton;
    private TextButton logoutButton;

    @Override
    public void act(float delta) {
        if (Chati.getInstance().isWorldListUpdated()) {
            updateWorldList();
        }
        super.act(delta);
    }

    @Override
    protected void create() {
        infoLabel.setText("Bitte wähle eine Aktion aus!");

        worldSelectLabel = new Label("Welt: ", Assets.SKIN);
        worldSelectBox = new SelectBox<>(Assets.SKIN);
        updateWorldList();

        joinWorldButton = new TextButton("Welt Beitreten", Assets.SKIN);
        joinWorldButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (worldSelectBox.getSelected() == null) {
                    infoLabel.setText("Bitte wähle eine Welt aus!");
                    return;
                }
                MenuScreen.getInstance().setPendingResponse(MenuResponse.JOIN_WORLD);
                Chati.getInstance().getServerSender()
                    .send(ServerSender.SendAction.WORLD_ACTION, worldSelectBox.getSelected().getContextId(), true);
            }
        });

        createWorldButton = new TextButton("Welt erstellen", Assets.SKIN);
        createWorldButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                MenuScreen.getInstance().setMenuTable(new WorldCreateTable());
            }
        });

        deleteWorldButton = new TextButton("Welt löschen", Assets.SKIN);
        deleteWorldButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (worldSelectBox.getSelected() == null) {
                    infoLabel.setText("Bitte wähle eine Welt aus!");
                    return;
                }
                MenuScreen.getInstance().setPendingResponse(MenuResponse.DELETE_WORLD);
                Chati.getInstance().getServerSender()
                        .send(ServerSender.SendAction.WORLD_DELETE, worldSelectBox.getSelected().getContextId());
            }
        });

        changeAvatarButton = new TextButton("Avatar ändern", Assets.SKIN);
        changeAvatarButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                MenuScreen.getInstance().setMenuTable(new AvatarSelectTable());
            }
        });

        profileSettingsButton = new TextButton("Konto verwalten", Assets.SKIN);
        profileSettingsButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                MenuScreen.getInstance().setMenuTable(new ProfileSettingsTable());
            }
        });

        logoutButton = new TextButton("Abmelden", Assets.SKIN);
        logoutButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Chati.getInstance().getServerSender().send(ServerSender.SendAction.PROFILE_LOGOUT, "", false);
                MenuScreen.getInstance().setMenuTable(new LoginTable());
            }
        });
    }

    @Override
    protected void setLayout() {
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().fillX().expandX();
        container.add(infoLabel).row();
        Table worldSelectContainer = new Table();
        worldSelectContainer.add(worldSelectLabel).width(ROW_WIDTH / 8f);
        worldSelectContainer.add(worldSelectBox).fillX().expandX();
        container.add(worldSelectContainer).row();
        Table worldButtonContainer = new Table();
        worldButtonContainer.defaults().height(ROW_HEIGHT).fillX().expandX();
        worldButtonContainer.add(joinWorldButton).spaceRight(SPACING);
        worldButtonContainer.add(createWorldButton).spaceRight(SPACING);
        worldButtonContainer.add(deleteWorldButton);
        container.add(worldButtonContainer).row();
        Table settingsButtonContainer = new Table();
        settingsButtonContainer.defaults().height(ROW_HEIGHT).fillX().expandX();
        settingsButtonContainer.add(changeAvatarButton).spaceRight(SPACING);
        settingsButtonContainer.add(profileSettingsButton);
        container.add(settingsButtonContainer).row();
        container.add(logoutButton);
        add(container).width(ROW_WIDTH);
    }

    @Override
    public void resetTextFields() {
    }

    private void updateWorldList() {
        worldSelectBox.setItems(MenuScreen.getInstance().getWorlds().toArray(new ContextEntry[0]));
    }
}
