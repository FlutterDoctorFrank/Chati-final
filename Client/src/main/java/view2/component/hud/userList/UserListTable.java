package view2.component.hud.userList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.role.Permission;
import model.user.*;
import view2.Assets;
import view2.Chati;
import view2.component.hud.HeadUpDisplay;
import view2.component.hud.HudMenuTable;

import java.util.*;

public class UserListTable extends HudMenuTable {

    private final Set<UserListEntry> friendEntries;
    private final Set<UserListEntry> activeUserEntries;
    private final Set<UserListEntry> bannedUserEntries;

    private TextButton friendTabButton;
    private TextButton activeUserTabButton;
    private TextButton bannedUserTabButton;
    private ScrollPane userListScrollPane;
    private Table userListContainer;

    public UserListTable() {
        this.friendEntries = new TreeSet<>();
        this.activeUserEntries = new TreeSet<>();
        this.bannedUserEntries = new TreeSet<>();
        create();
        setLayout();
    }

    @Override
    public void act(float delta) {
        if (Chati.getInstance().isUserInfoChanged() || Chati.getInstance().isWorldChanged()
                || Chati.getInstance().isRoomChanged()) {
            System.out.println("Methode wird aufgerufen");
            IInternUserView user = Chati.getInstance().getUserManager().getInternUserView();
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
                selectButton(friendTabButton);
            }
            if (user != null && user.isInCurrentWorld() && activeUserTabButton.isDisabled()) {
                enableButton(activeUserTabButton);
            }
            if (user != null && user.isInCurrentWorld()
                    && (user.hasPermission(Permission.BAN_USER) || user.hasPermission(Permission.BAN_MODERATOR))
                    && bannedUserTabButton.isDisabled()) {
                System.out.println("Diese hier auch");
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

    protected void create() {
        userListContainer = new Table();
        userListScrollPane = new ScrollPane(userListContainer, Assets.SKIN);

        friendTabButton = new TextButton("Freunde", Assets.getNewSkin());
        friendTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !friendTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showFriends();
                selectButton(friendTabButton);
                if (!activeUserTabButton.isDisabled()) {
                    unselectButton(activeUserTabButton);
                }
                if (!bannedUserTabButton.isDisabled()) {
                    unselectButton(bannedUserTabButton);
                }
            }
        });

        activeUserTabButton = new TextButton("Welt", Assets.getNewSkin());
        activeUserTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !activeUserTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showActiveUsers();
                selectButton(activeUserTabButton);
                if (!friendTabButton.isDisabled()) {
                    unselectButton(friendTabButton);
                }
                if (!bannedUserTabButton.isDisabled()) {
                    unselectButton(bannedUserTabButton);
                }
            }
        });

        bannedUserTabButton = new TextButton("Gesperrt", Assets.getNewSkin());
        bannedUserTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !bannedUserTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showBannedUsers();
                selectButton(bannedUserTabButton);
                if (!friendTabButton.isDisabled()) {
                    unselectButton(friendTabButton);
                }
                if (!activeUserTabButton.isDisabled()) {
                    unselectButton(activeUserTabButton);
                }
            }
        });

        IInternUserView user = Chati.getInstance().getUserManager().getInternUserView();
        if (user == null) {
            disableFriendsTab();
            disableActiveUsersTab();
            disableBannedUsersTab();
        } else {
            selectButton(friendTabButton);
            if (!user.isInCurrentWorld()) {
                disableActiveUsersTab();
                disableBannedUsersTab();
            } else if (!user.isInCurrentWorld() || !user.hasPermission(Permission.BAN_USER)
                    && !user.hasPermission(Permission.BAN_MODERATOR)) {
                disableBannedUsersTab();
            }
            showFriends();
        }

        ButtonGroup<TextButton> tabButtonGroup = new ButtonGroup<>();
        tabButtonGroup.setMinCheckCount(1);
        tabButtonGroup.setMaxCheckCount(1);
        tabButtonGroup.setUncheckLast(true);
        tabButtonGroup.add(friendTabButton);
        tabButtonGroup.add(activeUserTabButton);
        tabButtonGroup.add(bannedUserTabButton);
    }

    protected void setLayout() {
        top().right().padTop(HeadUpDisplay.BUTTON_SIZE);
        Window window = new Window("Benutzer", Assets.SKIN);
        window.setMovable(false);
        window.top();

        userListContainer.top();

        Table buttonContainer = new Table();
        buttonContainer.add(friendTabButton).fillX().expandX();
        buttonContainer.add(activeUserTabButton).fillX().expandX();
        buttonContainer.add(bannedUserTabButton).fillX().expandX();
        window.add(buttonContainer).fillX().expandX().row();

        window.add(userListScrollPane).fillX().expandX().fillY().expandY();
        add(window).width(HeadUpDisplay.HUD_MENU_TABLE_WIDTH).height(HeadUpDisplay.HUD_MENU_TABLE_HEIGHT);

        HeadUpDisplay.getInstance().getStage().setScrollFocus(userListScrollPane);
    }

    private void showFriends() {
        friendEntries.clear();
        IUserManagerView userManager = Chati.getInstance().getUserManager();
        if (userManager.isLoggedIn() && userManager.getInternUserView() != null) {
            Chati.getInstance().getUserManager().getFriends().values()
                    .forEach(friend -> friendEntries.add(new UserListEntry(friend)));
            layoutEntries(friendEntries);
        }
        // TEST ///////////////////////////////////////////////////////////////////////////////////////////////////////
/*
        String[] names = {"Jürgen", "Hans-Peter", "Detlef", "Olaf", "Markus", "Dietrich", "Dieter", "Siegbert", "Siegmund",
                "Joseph", "Ferdinand", "Alexander", "Analia", "Inkontinentia", "Vera Agina", "Agathe Bauer", "Bertha", "Hannelore",
                "Sieglinde", "Josephine", "Brigitte", "Luise-Annegret", "Alma-Dorothea", "Magdalena", "Brunhilde", "Herbert", "Gertrud", "Hiltrud",
                "Hagen", "Heinz", "Son-Goku", "Vegeta", "Axel Schweiß", "Rosa Schlüpfer", "Reinhold", "Mr.WasGehtSieDasAn", "Peter Enis",
                "Schwanzus-Longus", "G4meMason", "Franz Joseph", "Peter Silie", "Wilma Ficken", "Anna Bolika", "Anna Nass", "Deine Mutter"};
        List<String> namesList = Arrays.asList(names);
        Collections.shuffle(namesList, new Random());
        for (int i = 0; i<namesList.size(); i++) {
            User user = new User(UUID.randomUUID(), namesList.get(i), Status.values()[new Random().nextInt(Status.values().length)], null);
            try {
                if (new Random().nextBoolean()) {
                    user.setRoles(Context.getGlobal().getContextId(), Collections.singleton(Role.values()[new Random().nextInt(Role.values().length)]));
                }
                //user.setRoles(Context.getGlobal().getContextId(), Collections.singleton(Role.values()[new Random().nextInt(Role.values().length)]));
            } catch (ContextNotFoundException e) {
                e.printStackTrace();
            }
            UserListEntry entry = new UserListEntry(user);
            friendEntries.add(entry);
        }
        layoutEntries(friendEntries);
 */
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    private void showActiveUsers() {
        activeUserEntries.clear();
        IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
        if (internUser != null && internUser.isInCurrentWorld()) {
            Chati.getInstance().getUserManager().getActiveUsers().values()
                    .forEach(activeUser -> activeUserEntries.add(new UserListEntry(activeUser)));
            layoutEntries(activeUserEntries);
        }
    }

    private void showBannedUsers() {
        bannedUserEntries.clear();
        IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
        if (internUser != null && internUser.isInCurrentWorld()
                && (internUser.hasPermission(Permission.BAN_USER) || internUser.hasPermission(Permission.BAN_MODERATOR))) {
            Chati.getInstance().getUserManager().getBannedUsers().values()
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
        entries.forEach(entry -> userListContainer.add(entry).fillX().expandX().row());
    }
}
