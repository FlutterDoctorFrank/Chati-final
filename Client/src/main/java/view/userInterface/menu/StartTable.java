package view.userInterface.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import controller.network.ServerSender;
import org.jetbrains.annotations.NotNull;
import view.Chati;
import view.userInterface.*;
import view.Response;
import view.userInterface.actor.ChatiLabel;
import view.userInterface.actor.ChatiSelectBox;
import view.userInterface.actor.ChatiTable;
import view.userInterface.actor.ChatiTextButton;

/**
 * Eine Klasse, welche das Menü zum Auswählen eines zu betretenden Welt oder weiteren Menüs repräsentiert.
 */
public class StartTable extends ChatiTable {

    private final ChatiSelectBox<ContextEntry> worldSelectBox;

    /**
     * Erzeugt eine neue Instanz des StartTable.
     */
    public StartTable() {
        super("table.entry.choose-action");

        ChatiLabel worldSelectLabel = new ChatiLabel("menu.label.world");
        worldSelectBox = new ChatiSelectBox<>(ContextEntry::getName);
        worldSelectBox.setMaxListCount(MAX_LIST_COUNT);
        updateWorldList();

        ChatiTextButton joinWorldButton = new ChatiTextButton("menu.button.world-join", true);
        joinWorldButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (worldSelectBox.getSelected() == null) {
                    showMessage("table.entry.choose-world");
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.JOIN_WORLD);
                Chati.CHATI.send(ServerSender.SendAction.WORLD_ACTION, worldSelectBox.getSelected().getContextId(), true);
            }
        });

        ChatiTextButton createWorldButton = new ChatiTextButton("menu.button.world-create", true);
        createWorldButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new WorldCreateTable());
            }
        });

        ChatiTextButton deleteWorldButton = new ChatiTextButton("menu.button.world-delete", true);
        deleteWorldButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (worldSelectBox.getSelected() == null) {
                    showMessage("table.entry.choose-world");
                    return;
                }
                Chati.CHATI.getMenuScreen().setPendingResponse(Response.DELETE_WORLD);
                Chati.CHATI.send(ServerSender.SendAction.WORLD_DELETE, worldSelectBox.getSelected().getContextId());
            }
        });

        ChatiTextButton changeAvatarButton = new ChatiTextButton("menu.button.change-avatar", true);
        changeAvatarButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new AvatarSelectTable());
            }
        });

        ChatiTextButton profileSettingsButton = new ChatiTextButton("menu.button.manage-account", true);
        profileSettingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getMenuScreen().setMenuTable(new ProfileSettingsTable());
            }
        });

        ChatiTextButton logoutButton = new ChatiTextButton("menu.button.logout", true);
        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
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

        // Translatable register
        translatables.add(worldSelectLabel);
        translatables.add(joinWorldButton);
        translatables.add(createWorldButton);
        translatables.add(deleteWorldButton);
        translatables.add(changeAvatarButton);
        translatables.add(profileSettingsButton);
        translatables.add(logoutButton);
        translatables.trimToSize();
    }

    @Override
    public void act(final float delta) {
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
        worldSelectBox.setItems(Chati.CHATI.getUserManager().getWorlds().values().stream()
                .map(world -> new ContextEntry(world.getContextId(), world.getContextName()))
                .distinct().sorted().toArray(ContextEntry[]::new));
    }
}
