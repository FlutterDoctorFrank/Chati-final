package view2.userInterface.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import view2.Chati;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ContextEntry;
import view2.Response;

/**
 * Eine Klasse, welche das Menü zum Auswählen eines zu betretenden Welt oder weiteren Menüs repräsentiert.
 */
public class StartTable extends MenuTable {

    private final SelectBox<ContextEntry> worldSelectBox;

    /**
     * Erzeugt eine neue Instanz des StartTable.
     */
    public StartTable() {
        infoLabel.setText("Bitte wähle eine Aktion aus!");

        Label worldSelectLabel = new Label("Welt: ", Chati.CHATI.getSkin());
        worldSelectBox = new SelectBox<>(Chati.CHATI.getSkin());
        updateWorldList();

        ChatiTextButton joinWorldButton = new ChatiTextButton("Welt Beitreten", true);
        joinWorldButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (worldSelectBox.getSelected() == null) {
                    infoLabel.setText("Bitte wähle eine Welt aus!");
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.JOIN_WORLD);
                Chati.CHATI.send(ServerSender.SendAction.WORLD_ACTION, worldSelectBox.getSelected().getContextId(), true);
            }
        });

        ChatiTextButton createWorldButton = new ChatiTextButton("Welt erstellen", true);
        createWorldButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new WorldCreateTable());
            }
        });

        ChatiTextButton deleteWorldButton = new ChatiTextButton("Welt löschen", true);
        deleteWorldButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (worldSelectBox.getSelected() == null) {
                    infoLabel.setText("Bitte wähle eine Welt aus!");
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.DELETE_WORLD);
                Chati.CHATI.send(ServerSender.SendAction.WORLD_DELETE, worldSelectBox.getSelected().getContextId());
            }
        });

        ChatiTextButton changeAvatarButton = new ChatiTextButton("Avatar ändern", true);
        changeAvatarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new AvatarSelectTable());
            }
        });

        ChatiTextButton profileSettingsButton = new ChatiTextButton("Konto verwalten", true);
        profileSettingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new ProfileSettingsTable());
            }
        });

        ChatiTextButton logoutButton = new ChatiTextButton("Abmelden", true);
        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Chati.CHATI.send(ServerSender.SendAction.PROFILE_LOGOUT, "", false);
                Chati.CHATI.getMenuScreen().setMenuTable(new LoginTable());
            }
        });

        // Layout
        Table container = new Table();
        container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
        container.add(infoLabel).row();
        Table worldSelectContainer = new Table();
        worldSelectContainer.add(worldSelectLabel).width(ROW_WIDTH / 8f);
        worldSelectContainer.add(worldSelectBox).growX();
        container.add(worldSelectContainer).row();
        Table worldButtonContainer = new Table();
        worldButtonContainer.defaults().colspan(3).height(ROW_HEIGHT).growX();
        worldButtonContainer.add(joinWorldButton).padRight(SPACING / 2);
        worldButtonContainer.add(createWorldButton).padLeft(SPACING / 2).padRight(SPACING / 2);
        worldButtonContainer.add(deleteWorldButton).padLeft(SPACING / 2);
        container.add(worldButtonContainer).row();
        Table settingsButtonContainer = new Table();
        settingsButtonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
        settingsButtonContainer.add(changeAvatarButton).padRight(SPACING / 2);
        settingsButtonContainer.add(profileSettingsButton).padLeft(SPACING / 2);
        container.add(settingsButtonContainer).row();
        container.add(logoutButton);
        add(container).width(ROW_WIDTH);
    }

    @Override
    public void act(float delta) {
        if (Chati.CHATI.isWorldListChanged()) {
            updateWorldList();
        }
        super.act(delta);
    }

    @Override
    public void resetTextFields() {
    }

    /**
     * Aktualisiert die Liste der angezeigten Welten.
     */
    private void updateWorldList() {
        worldSelectBox.setItems(Chati.CHATI.getWorlds().toArray(new ContextEntry[0]));
    }
}
