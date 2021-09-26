package view2.userInterface.hud.userList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.role.Permission;
import model.user.IInternUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.hud.HudMenuWindow;
import java.util.Set;
import java.util.TreeSet;

/**
 * Eine Klasse, welche das Benutzermenü des HeadUpDisplay repräsentiert.
 */
public class UserListWindow extends HudMenuWindow {

    private final Set<UserListEntry> friendEntries;
    private final Set<UserListEntry> activeUserEntries;
    private final Set<UserListEntry> bannedUserEntries;

    private final ChatiTextButton friendTabButton;
    private final ChatiTextButton activeUserTabButton;
    private final ChatiTextButton bannedUserTabButton;
    private final ScrollPane userListScrollPane;
    private final Table userListContainer;

    /**
     * Erzeugt eine neue Instanz des UserListWindow.
     */
    public UserListWindow() {
        super("window.title.users");
        this.friendEntries = new TreeSet<>();
        this.activeUserEntries = new TreeSet<>();
        this.bannedUserEntries = new TreeSet<>();

        userListContainer = new Table();
        userListScrollPane = new ScrollPane(userListContainer, Chati.CHATI.getSkin());
        userListScrollPane.setFadeScrollBars(false);
        userListScrollPane.setOverscroll(false, false);
        userListScrollPane.setScrollingDisabled(true, false);

        friendTabButton = new ChatiTextButton("menu.button.friends", false);
        friendTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(@NotNull final InputEvent event, final float x, final float y,
                                     final int pointer, final int button) {
                return !friendTabButton.isChecked();
            }
            @Override
            public void touchUp(@NotNull final InputEvent event, final float x, final float y,
                                final int pointer, final int button) {
                showFriends();
                friendTabButton.setChecked(true);
            }
        });

        activeUserTabButton = new ChatiTextButton("menu.button.world", false);
        activeUserTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(@NotNull final InputEvent event, final float x, final float y,
                                     final int pointer, final int button) {
                return !activeUserTabButton.isChecked();
            }
            @Override
            public void touchUp(@NotNull final InputEvent event, final float x, final float y,
                                final int pointer, final int button) {
                showActiveUsers();
                activeUserTabButton.setChecked(true);
            }
        });

        bannedUserTabButton = new ChatiTextButton("menu.button.banned", false);
        bannedUserTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(@NotNull final InputEvent event, final float x, final float y,
                                     final int pointer, final int button) {
                return !bannedUserTabButton.isChecked();
            }
            @Override
            public void touchUp(@NotNull final InputEvent event, final float x, final float y,
                                final int pointer, final int button) {
                showBannedUsers();
                bannedUserTabButton.setChecked(true);
            }
        });

        ButtonGroup<ChatiTextButton> tabButtonGroup = new ButtonGroup<>();
        tabButtonGroup.setMinCheckCount(1);
        tabButtonGroup.setMaxCheckCount(1);
        tabButtonGroup.setUncheckLast(true);
        tabButtonGroup.add(friendTabButton);
        tabButtonGroup.add(activeUserTabButton);
        tabButtonGroup.add(bannedUserTabButton);

        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser == null) {
            disableFriendsTab();
            disableActiveUsersTab();
            disableBannedUsersTab();
        } else {
            friendTabButton.setChecked(true);
            if (!internUser.isInCurrentWorld()) {
                disableActiveUsersTab();
                disableBannedUsersTab();
            } else if (!internUser.isInCurrentWorld() || !internUser.hasPermission(Permission.BAN_USER)
                    && !internUser.hasPermission(Permission.BAN_MODERATOR)) {
                disableBannedUsersTab();
            }
            showFriends();
        }

        // Layout
        userListContainer.top();
        Table buttonContainer = new Table();
        buttonContainer.defaults().colspan(3).growX();
        buttonContainer.add(friendTabButton, activeUserTabButton, bannedUserTabButton);
        add(buttonContainer).growX().row();
        add(userListScrollPane).grow();

        // Translatable register
        translates.add(friendTabButton);
        translates.add(activeUserTabButton);
        translates.add(bannedUserTabButton);
        translates.trimToSize();
    }

    @Override
    public void act(final float delta) {
        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()
                || Chati.CHATI.isRoomChanged()) {
            IInternUserView user = Chati.CHATI.getInternUser();
            if (user == null && !friendTabButton.isDisabled()) {
                disableBannedUsersTab();
                disableActiveUsersTab();
                disableFriendsTab();
            } else if (user != null && !user.isInCurrentWorld() && !activeUserTabButton.isDisabled()) {
                disableBannedUsersTab();
                disableActiveUsersTab();
            } else if (user != null && user.isInCurrentWorld()
                    && !(user.hasPermission(Permission.BAN_USER) || user.hasPermission(Permission.BAN_MODERATOR))
                    && !bannedUserTabButton.isDisabled()) {
                disableBannedUsersTab();
            }

            if (user != null && friendTabButton.isDisabled()) {
                enableButton(friendTabButton);
                friendTabButton.setChecked(true);
            }
            if (user != null && user.isInCurrentWorld() && activeUserTabButton.isDisabled()) {
                enableButton(activeUserTabButton);
            }
            if (user != null && user.isInCurrentWorld()
                    && (user.hasPermission(Permission.BAN_USER) || user.hasPermission(Permission.BAN_MODERATOR))
                    && bannedUserTabButton.isDisabled()) {
                enableButton(bannedUserTabButton);
            }

            if (!friendTabButton.isDisabled() && friendTabButton.isChecked()) {
                showFriends();
            } else if (!activeUserTabButton.isDisabled() && activeUserTabButton.isChecked()) {
                showActiveUsers();
            } else if (!bannedUserTabButton.isDisabled() && bannedUserTabButton.isChecked()) {
                showBannedUsers();
            }
        }
        super.act(delta);
    }

    @Override
    public void focus() {
        super.focus();
        if (getStage() != null) {
            getStage().setScrollFocus(userListScrollPane);
        }
    }

    /**
     * Zeigt die Liste alle befreundeten Benutzer an.
     */
    private void showFriends() {
        friendEntries.clear();
        if (Chati.CHATI.getInternUser() != null) {
            Chati.CHATI.getUserManager().getFriends().values()
                    .forEach(friend -> friendEntries.add(new UserListEntry(friend)));
            layoutEntries(friendEntries);
        }
    }

    /**
     * Zeigt die Liste aller in der momentanen Welt aktiven Benutzer an.
     */
    private void showActiveUsers() {
        activeUserEntries.clear();
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null && internUser.isInCurrentWorld()) {
            Chati.CHATI.getUserManager().getActiveUsers().values()
                    .forEach(activeUser -> activeUserEntries.add(new UserListEntry(activeUser)));
            layoutEntries(activeUserEntries);
        }
    }

    /**
     * Zeigt die Liste aller in der momentanen Welt gesperrten Benutzer an.
     */
    private void showBannedUsers() {
        bannedUserEntries.clear();
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null && internUser.isInCurrentWorld()
                && (internUser.hasPermission(Permission.BAN_USER) || internUser.hasPermission(Permission.BAN_MODERATOR))) {
            Chati.CHATI.getUserManager().getBannedUsers().values()
                    .forEach(bannedUser -> bannedUserEntries.add(new UserListEntry(bannedUser)));
            layoutEntries(bannedUserEntries);
        }
    }

    /**
     * Deaktiviert den Tab zur Anzeige der Liste der Freunde.
     */
    private void disableFriendsTab() {
        disableButton(friendTabButton);
        friendEntries.clear();
        userListContainer.clearChildren();
    }

    /**
     * Deaktiviert den Tab zur Anzeige der Liste der in der Welt aktiven Benutzer.
     */
    private void disableActiveUsersTab() {
        if (activeUserTabButton.isChecked() && !friendTabButton.isDisabled()) {
            activeUserTabButton.setChecked(false);
            friendTabButton.setChecked(true);
            showFriends();
        }
        disableButton(activeUserTabButton);
        activeUserEntries.clear();
    }

    /**
     * Deaktiviert den Tab zur Anzeige der Liste der in der Welt gesperrten Benutzer.
     */
    private void disableBannedUsersTab() {
        if (bannedUserTabButton.isChecked() && !friendTabButton.isDisabled()) {
            bannedUserTabButton.setChecked(false);
            friendTabButton.setChecked(true);
            showFriends();
        }
        disableButton(bannedUserTabButton);
        bannedUserEntries.clear();
    }

    /**
     * Zeigt die übergebenen Einträge in der Liste an.
     * @param entries Anzuzeigende Einträge.
     */
    private void layoutEntries(@NotNull final Set<UserListEntry> entries) {
        userListContainer.clearChildren();
        entries.forEach(entry -> userListContainer.add(entry).growX().row());
    }
}
