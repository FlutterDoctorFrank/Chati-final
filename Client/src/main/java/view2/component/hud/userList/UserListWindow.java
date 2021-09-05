package view2.component.hud.userList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.role.Permission;
import model.user.IInternUserView;
import view2.Chati;
import view2.component.ChatiTextButton;
import view2.component.hud.HudMenuWindow;

import java.util.Set;
import java.util.TreeSet;

public class UserListWindow extends HudMenuWindow {

    private final Set<UserListEntry> friendEntries;
    private final Set<UserListEntry> activeUserEntries;
    private final Set<UserListEntry> bannedUserEntries;

    private final ChatiTextButton friendTabButton;
    private final ChatiTextButton activeUserTabButton;
    private final ChatiTextButton bannedUserTabButton;
    private final Table userListContainer;

    public UserListWindow() {
        super("Benutzer");
        this.friendEntries = new TreeSet<>();
        this.activeUserEntries = new TreeSet<>();
        this.bannedUserEntries = new TreeSet<>();

        userListContainer = new Table();
        ScrollPane userListScrollPane = new ScrollPane(userListContainer, Chati.CHATI.getSkin());

        friendTabButton = new ChatiTextButton("Freunde", false);
        friendTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !friendTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showFriends();
                friendTabButton.setChecked(true);
            }
        });

        activeUserTabButton = new ChatiTextButton("Welt", false);
        activeUserTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !activeUserTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showActiveUsers();
                activeUserTabButton.setChecked(true);
            }
        });

        bannedUserTabButton = new ChatiTextButton("Gesperrt", false);
        bannedUserTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !bannedUserTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
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
        Chati.CHATI.getScreen().getStage().setScrollFocus(userListScrollPane);
    }

    @Override
    public void act(float delta) {
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

    private void showFriends() {
        friendEntries.clear();
        if (Chati.CHATI.getInternUser() != null) {
            Chati.CHATI.getUserManager().getFriends().values()
                    .forEach(friend -> friendEntries.add(new UserListEntry(friend)));
            layoutEntries(friendEntries);
        }
    }

    private void showActiveUsers() {
        activeUserEntries.clear();
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null && internUser.isInCurrentWorld()) {
            Chati.CHATI.getUserManager().getActiveUsers().values()
                    .forEach(activeUser -> activeUserEntries.add(new UserListEntry(activeUser)));
            layoutEntries(activeUserEntries);
        }
    }

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

    private void disableFriendsTab() {
        disableButton(friendTabButton);
        friendEntries.clear();
        userListContainer.clearChildren();
    }

    private void disableActiveUsersTab() {
        if (activeUserTabButton.isChecked() && !friendTabButton.isDisabled()) {
            activeUserTabButton.setChecked(false);
            friendTabButton.setChecked(true);
            showFriends();
        }
        disableButton(activeUserTabButton);
        activeUserEntries.clear();
    }

    private void disableBannedUsersTab() {
        if (bannedUserTabButton.isChecked() && !friendTabButton.isDisabled()) {
            bannedUserTabButton.setChecked(false);
            friendTabButton.setChecked(true);
            showFriends();
        }
        disableButton(bannedUserTabButton);
        bannedUserEntries.clear();
    }

    private void layoutEntries(Set<UserListEntry> entries) {
        userListContainer.clearChildren();
        entries.forEach(entry -> userListContainer.add(entry).growX().row());
    }
}
