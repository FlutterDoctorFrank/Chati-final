package view2.userInterface.hud.userList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import model.role.Permission;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiImageButton;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTooltip;
import view2.userInterface.hud.HudMenuWindow;
import java.util.Set;
import java.util.TreeSet;

/**
 * Eine Klasse, welche das Benutzermenü des HeadUpDisplay repräsentiert.
 */
public class UserListWindow extends HudMenuWindow {

    private static final float BUTTON_SIZE = 30;

    private final Set<UserListEntry> currentEntries;
    private final ChatiTextButton friendTabButton;
    private final ChatiTextButton activeUserTabButton;
    private final ChatiTextButton bannedUserTabButton;
    private final ScrollPane userListScrollPane;
    private final Table userListContainer;
    private final ChatiLabel onlineUsersCountLabel;
    private final ChatiImageButton roomMessageButton;
    private final ChatiImageButton worldMessageButton;

    /**
     * Erzeugt eine neue Instanz des UserListWindow.
     */
    public UserListWindow() {
        super("window.title.users");
        this.currentEntries = new TreeSet<>();

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

        onlineUsersCountLabel = new ChatiLabel("window.user.info", 0, 0);

        roomMessageButton = new ChatiImageButton(Chati.CHATI.getDrawable("current_room"),
                Chati.CHATI.getDrawable("current_room"), Chati.CHATI.getDrawable("current_room_disabled"));
        roomMessageButton.addListener(new ChatiTooltip("hud.tooltip.room-message"));
        roomMessageButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getHeadUpDisplay().showChatWindow("\\room ");
            }
        });
        roomMessageButton.setDisabled(true);
        roomMessageButton.setTouchable(Touchable.disabled);

        worldMessageButton = new ChatiImageButton(Chati.CHATI.getDrawable("current_world"),
                Chati.CHATI.getDrawable("current_world"), Chati.CHATI.getDrawable("current_world_disabled"));
        worldMessageButton.addListener(new ChatiTooltip("hud.tooltip.world-message"));
        worldMessageButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                Chati.CHATI.getHeadUpDisplay().showChatWindow("\\world ");
            }
        });
        worldMessageButton.setDisabled(true);
        worldMessageButton.setTouchable(Touchable.disabled);

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
        Table tabContainer = new Table();
        tabContainer.defaults().colspan(3).growX();
        tabContainer.add(friendTabButton, activeUserTabButton, bannedUserTabButton);
        add(tabContainer).growX().row();
        Table buttonContainer = new Table();
        buttonContainer.defaults().size(BUTTON_SIZE).right().padLeft(SPACE / 2).padRight(SPACE / 2)
                .padBottom(SPACE / 4).padTop(SPACE / 4);
        onlineUsersCountLabel.setAlignment(Align.left, Align.left);
        buttonContainer.add(onlineUsersCountLabel).left().growX();
        buttonContainer.add(roomMessageButton, worldMessageButton);
        add(buttonContainer).growX().row();
        add(userListScrollPane).grow();

        // Translatable register
        translatables.add(friendTabButton);
        translatables.add(activeUserTabButton);
        translatables.add(bannedUserTabButton);
        translatables.add(onlineUsersCountLabel);
        translatables.trimToSize();
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
        if (getStage() != null) {
            getStage().setScrollFocus(userListScrollPane);
        }
    }

    /**
     * Zeigt die Liste alle befreundeten Benutzer an.
     */
    private void showFriends() {
        currentEntries.clear();
        if (Chati.CHATI.getInternUser() != null) {
            Chati.CHATI.getUserManager().getFriends().values()
                    .forEach(friend -> currentEntries.add(new UserListEntry(friend)));
            layoutEntries();
        }
    }

    /**
     * Zeigt die Liste aller in der momentanen Welt aktiven Benutzer an.
     */
    private void showActiveUsers() {
        currentEntries.clear();
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null && internUser.isInCurrentWorld()) {
            Chati.CHATI.getUserManager().getActiveUsers().values()
                    .forEach(activeUser -> currentEntries.add(new UserListEntry(activeUser)));
            layoutEntries();
        }
    }

    /**
     * Zeigt die Liste aller in der momentanen Welt gesperrten Benutzer an.
     */
    private void showBannedUsers() {
        currentEntries.clear();
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null && internUser.isInCurrentWorld()
                && (internUser.hasPermission(Permission.BAN_USER) || internUser.hasPermission(Permission.BAN_MODERATOR))) {
            Chati.CHATI.getUserManager().getBannedUsers().values()
                    .forEach(bannedUser -> currentEntries.add(new UserListEntry(bannedUser)));
            layoutEntries();
        }
    }

    /**
     * Deaktiviert den Tab zur Anzeige der Liste der Freunde.
     */
    private void disableFriendsTab() {
        disableButton(friendTabButton);
        currentEntries.clear();
        userListContainer.clearChildren();
    }

    /**
     * Deaktiviert den Tab zur Anzeige der Liste der in der Welt aktiven Benutzer.
     */
    private void disableActiveUsersTab() {
        disableButton(activeUserTabButton);
        currentEntries.clear();
        if (activeUserTabButton.isChecked() && !friendTabButton.isDisabled()) {
            activeUserTabButton.setChecked(false);
            friendTabButton.setChecked(true);
            showFriends();
        }
    }

    /**
     * Deaktiviert den Tab zur Anzeige der Liste der in der Welt gesperrten Benutzer.
     */
    private void disableBannedUsersTab() {
        disableButton(bannedUserTabButton);
        currentEntries.clear();
        if (bannedUserTabButton.isChecked() && !friendTabButton.isDisabled()) {
            bannedUserTabButton.setChecked(false);
            friendTabButton.setChecked(true);
            showFriends();
        }
    }

    /**
     * Zeigt die übergebenen Einträge in der Liste an.
     */
    private void layoutEntries() {
        userListContainer.clearChildren();
        currentEntries.forEach(entry -> userListContainer.add(entry).growX().row());

        updateUserCount();

        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser == null || !internUser.canContactRoom()) {
            roomMessageButton.setDisabled(true);
            roomMessageButton.setTouchable(Touchable.disabled);
        } else {
            roomMessageButton.setDisabled(false);
            roomMessageButton.setTouchable(Touchable.enabled);
        }
        if (internUser == null || !internUser.canContactWorld()) {
            worldMessageButton.setDisabled(true);
            worldMessageButton.setTouchable(Touchable.disabled);
        } else {
            worldMessageButton.setDisabled(false);
            worldMessageButton.setTouchable(Touchable.enabled);
        }
    }

    @Override
    public void translate() {
        super.translate();
        updateUserCount();
    }

    /**
     * Aktualisiert die Anzeige der Benutzeranzahl.
     */
    private void updateUserCount() {
        int onlineUsersCount = (int) currentEntries.stream().map(UserListEntry::getUser)
                .filter(IUserView::isOnline).count();
        onlineUsersCountLabel.setText(Chati.CHATI.getLocalization().format("window.user.info",
                currentEntries.size(), onlineUsersCount));
    }
}
