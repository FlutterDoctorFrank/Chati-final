package view2.component.hud.userList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.role.Permission;
import model.user.IInternUserView;
import model.user.Status;
import model.user.User;
import view2.Chati;
import view2.component.ChatiTable;
import view2.component.hud.HeadUpDisplay;

import java.util.*;
import java.util.List;

public class UserListTable extends ChatiTable {

    private final Set<UserListEntry> friendEntries;
    private final Set<UserListEntry> activeUserEntries;
    private final Set<UserListEntry> bannedUserEntries;

    private ButtonGroup<TextButton> tabButtonGroup;
    private TextButton friendTabButton;
    private TextButton activeUserTabButton;
    private TextButton bannedUserTabButton;
    private ScrollPane userListScrollPane;
    private Table userListContainer;

    public UserListTable() {
        this.friendEntries = new HashSet<>();
        this.activeUserEntries = new HashSet<>();
        this.bannedUserEntries = new HashSet<>();
        create();
        setLayout();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
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
            enableFriendsTab();
        } else if (user != null && user.isInCurrentWorld() && activeUserTabButton.isDisabled()) {
            enableActiveUsersTab();
        }
        if (user != null && user.isInCurrentWorld()
                && (user.hasPermission(Permission.BAN_USER) || user.hasPermission(Permission.BAN_MODERATOR))
                && bannedUserTabButton.isDisabled()) {
            enableBannedUsersTab();
        }

        if (Chati.getInstance().isUserInfoChanged()) {
            loadUserEntries();
            if (!friendTabButton.isDisabled() && friendTabButton.isChecked()) {
                layoutEntries(friendEntries);
            } else if (!activeUserTabButton.isDisabled() && activeUserTabButton.isChecked()) {
                layoutEntries(activeUserEntries);
            } else if (!bannedUserTabButton.isDisabled() && bannedUserTabButton.isChecked()) {
                layoutEntries(bannedUserEntries);
            }
        }
        super.draw(batch, parentAlpha);
    }

    protected void create() {
        tabButtonGroup = new ButtonGroup<>();
        tabButtonGroup.setMinCheckCount(1);
        tabButtonGroup.setMaxCheckCount(1);
        tabButtonGroup.setUncheckLast(true);

        userListContainer = new Table(Chati.SKIN);
        userListScrollPane = new ScrollPane(userListContainer, Chati.SKIN);

        Skin friendTabButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        friendTabButton = new TextButton("Freunde", friendTabButtonSkin);
        friendTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !friendTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                loadUserEntries();
                layoutEntries(friendEntries);
                friendTabButton.getLabel().setColor(Color.MAGENTA);
                friendTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
                if (!activeUserTabButton.isDisabled()) {
                    activeUserTabButton.getLabel().setColor(Color.WHITE);
                    activeUserTabButton.getStyle().up = HeadUpDisplay.UNPRESSED_BUTTON_IMAGE;
                }
                if (!bannedUserTabButton.isDisabled()) {
                    bannedUserTabButton.getLabel().setColor(Color.WHITE);
                    bannedUserTabButton.getStyle().up = HeadUpDisplay.UNPRESSED_BUTTON_IMAGE;
                }
            }
        });

        Skin activeUserTabButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        activeUserTabButton = new TextButton("Welt", activeUserTabButtonSkin);
        activeUserTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !activeUserTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                loadUserEntries();
                layoutEntries(activeUserEntries);
                activeUserTabButton.getLabel().setColor(Color.MAGENTA);
                activeUserTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
                if (!friendTabButton.isDisabled()) {
                    friendTabButton.getLabel().setColor(Color.WHITE);
                    friendTabButton.getStyle().up = HeadUpDisplay.UNPRESSED_BUTTON_IMAGE;
                }
                if (!bannedUserTabButton.isDisabled()) {
                    bannedUserTabButton.getLabel().setColor(Color.WHITE);
                    bannedUserTabButton.getStyle().up = HeadUpDisplay.UNPRESSED_BUTTON_IMAGE;
                }
            }
        });

        Skin bannedUserTabButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        bannedUserTabButton = new TextButton("Gesperrt", bannedUserTabButtonSkin);
        bannedUserTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !bannedUserTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                loadUserEntries();
                layoutEntries(bannedUserEntries);
                bannedUserTabButton.getLabel().setColor(Color.MAGENTA);
                bannedUserTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
                if (!friendTabButton.isDisabled()) {
                    friendTabButton.getLabel().setColor(Color.WHITE);
                    friendTabButton.getStyle().up = HeadUpDisplay.UNPRESSED_BUTTON_IMAGE;
                }
                if (!activeUserTabButton.isDisabled()) {
                    activeUserTabButton.getLabel().setColor(Color.WHITE);
                    activeUserTabButton.getStyle().up = HeadUpDisplay.UNPRESSED_BUTTON_IMAGE;
                }
            }
        });

        tabButtonGroup.add(friendTabButton);
        tabButtonGroup.add(activeUserTabButton);
        tabButtonGroup.add(bannedUserTabButton);

        IInternUserView user = Chati.getInstance().getUserManager().getInternUserView();
        if (user == null) {
            disableFriendsTab();
            disableActiveUsersTab();
            disableBannedUsersTab();
        } else {
            friendTabButton.getLabel().setColor(Color.MAGENTA);
            friendTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
            if (!user.isInCurrentWorld()) {
                disableActiveUsersTab();
                disableBannedUsersTab();
            } else if (!user.hasPermission(Permission.BAN_USER) || !user.hasPermission(Permission.BAN_MODERATOR)) {
                disableBannedUsersTab();
            }

            loadUserEntries();
            layoutEntries(friendEntries);
        }
    }

    protected void setLayout() {
        top().right().padTop(HeadUpDisplay.BUTTON_SIZE);
        Window window = new Window("Benutzer", Chati.SKIN);
        window.setMovable(false);
        window.top();

        userListContainer.top();

        // TEST ///////////////////////////////////////////////////////////////////////////////////////////////////////
        String[] names = {"Jürgen", "Hans-Peter", "Detlef", "Olaf", "Markus", "Dietrich", "Dieter", "Siegbert", "Siegmund",
            "Joseph", "Ferdinand", "Alexander", "Adolf H", "Analia", "Inkontinentia", "Vera Agina", "Agathe Bauer", "Bertha", "Hannelore",
            "Sieglinde", "Josephine", "Brigitte", "Luise-Annegret", "Alma-Dorothea", "Magdalena", "Brunhilde", "Herbert", "Gertrud", "Hiltrud",
            "Hagen", "Heinz", "Son-Goku", "Vegeta", "Axel Schweiß", "Rosa Schlüpfer", "Reinhold", "Mr.WasGehtSieDasAn", "Peter Enis",
            "Schwanzus-Longus", "G4meMason", "Franz Joseph", "Peter Silie", "Wilma Ficken", "Anna Bolika", "Anna Nass", "Deine Mutter"};
        List<String> namesList = Arrays.asList(names);
        Collections.shuffle(namesList, new Random());
        for (int i = 0; i<namesList.size(); i++) {
            User user = new User(UUID.randomUUID(), namesList.get(i), Status.values()[new Random().nextInt(Status.values().length)], null);
            UserListEntry entry = new UserListEntry(user);
            userListContainer.top().left().add(entry).fillX().expandX().row();
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Table buttonContainer = new Table(Chati.SKIN);
        buttonContainer.add(friendTabButton).fillX().expandX();
        buttonContainer.add(activeUserTabButton).fillX().expandX();
        buttonContainer.add(bannedUserTabButton).fillX().expandX();
        window.add(buttonContainer).fillX().expandX().row();

        window.add(userListScrollPane).fillX().expandX().fillY().expandY();
        add(window).width(HeadUpDisplay.HUD_MENU_TABLE_WIDTH).height(HeadUpDisplay.HUD_MENU_TABLE_HEIGHT);

        Chati.getInstance().getMenuScreen().getStage().setScrollFocus(userListScrollPane);
    }

    private void loadUserEntries() {
        friendEntries.clear();
        activeUserEntries.clear();
        bannedUserEntries.clear();
        IInternUserView user = Chati.getInstance().getUserManager().getInternUserView();
        if (user != null) {
            Chati.getInstance().getUserManager().getFriends().values()
                    .forEach(friend -> friendEntries.add(new UserListEntry(friend)));
            if (user.isInCurrentWorld()) {
                Chati.getInstance().getUserManager().getActiveUsers().values()
                        .forEach(activeUser -> activeUserEntries.add(new UserListEntry(activeUser)));
                if (user.hasPermission(Permission.BAN_USER) || user.hasPermission(Permission.BAN_MODERATOR)) {
                    Chati.getInstance().getUserManager().getActiveUsers().values()
                            .forEach(bannedUser -> bannedUserEntries.add(new UserListEntry(bannedUser)));
                }
            }
        }
    }

    private void enableFriendsTab() {
        friendTabButton.setDisabled(false);
        friendTabButton.setTouchable(Touchable.enabled);
        friendTabButton.getLabel().setColor(Color.MAGENTA);
        friendTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
    }

    private void enableActiveUsersTab() {
        activeUserTabButton.setDisabled(false);
        activeUserTabButton.setTouchable(Touchable.enabled);
        activeUserTabButton.getLabel().setColor(Color.WHITE);
        activeUserTabButton.getStyle().up = HeadUpDisplay.UNPRESSED_BUTTON_IMAGE;
    }

    private void enableBannedUsersTab() {
        bannedUserTabButton.setDisabled(false);
        bannedUserTabButton.setTouchable(Touchable.enabled);
        bannedUserTabButton.getLabel().setColor(Color.WHITE);
        bannedUserTabButton.getStyle().up = HeadUpDisplay.UNPRESSED_BUTTON_IMAGE;
    }

    private void disableFriendsTab() {
        friendTabButton.setDisabled(true);
        friendTabButton.setTouchable(Touchable.disabled);
        friendTabButton.getLabel().setColor(Color.DARK_GRAY);
        friendTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
        friendEntries.clear();
        userListContainer.clearChildren();
    }

    private void disableActiveUsersTab() {
        if (activeUserTabButton.isChecked() && !friendTabButton.isDisabled()) {
            activeUserTabButton.setChecked(false);
            friendTabButton.setChecked(true);
            layoutEntries(friendEntries);
        }
        activeUserTabButton.setDisabled(true);
        activeUserTabButton.setTouchable(Touchable.disabled);
        activeUserTabButton.getLabel().setColor(Color.DARK_GRAY);
        activeUserTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
        activeUserEntries.clear();
    }

    private void disableBannedUsersTab() {
        if (bannedUserTabButton.isChecked() && !friendTabButton.isDisabled()) {
            bannedUserTabButton.setChecked(false);
            friendTabButton.setChecked(true);
            layoutEntries(friendEntries);
        }
        bannedUserTabButton.setDisabled(true);
        bannedUserTabButton.setTouchable(Touchable.disabled);
        bannedUserTabButton.getLabel().setColor(Color.DARK_GRAY);
        bannedUserTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
        bannedUserEntries.clear();
    }

    private void layoutEntries(Set<UserListEntry> entries) {
        userListContainer.clearChildren();
        entries.forEach(entry -> userListContainer.add(entry).fillX().expandX().row());
    }
}
