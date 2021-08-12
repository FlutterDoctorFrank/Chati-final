package view2.component.hud.userList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import model.user.Status;
import model.user.User;
import view2.Chati;
import view2.component.hud.HeadUpDisplay;
import view2.component.hud.HudMenuTable;

import java.util.*;
import java.util.List;

public class UserListTable extends HudMenuTable {

    private ButtonGroup<TextButton> tabButtonGroup;
    private TextButton friendTabButton;
    private TextButton activeUserTabButton;
    private TextButton bannedUserTabButton;
    private ScrollPane userListScrollPane;
    private Table userListContainer;

    private final Set<UserListEntry> friendEntries;
    private final Set<UserListEntry> activeUserEntries;
    private final Set<UserListEntry> bannedUserEntries;

    public UserListTable() {
        this.friendEntries = new HashSet<>();
        this.activeUserEntries = new HashSet<>();
        this.bannedUserEntries = new HashSet<>();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        /*
        IUserManagerView userManager = Chati.getInstance().getUserManager();
        if (Chati.getInstance().isUserInfoChanged()) {
            if (friendTabButton.isDisabled() && userManager.getInternUserView() != null) {
                enableFriendTab();
            }
            if (activeUserTabButton.isDisabled() && userManager.getInternUserView() != null
                && userManager.getInternUserView().isInCurrentWorld()) {
                enableActiveUserTab();
            }
            if (bannedUserTabButton.isDisabled() && userManager.getInternUserView() != null
                && (userManager.getInternUserView().hasPermission(Permission.BAN_USER)
                || userManager.getInternUserView().hasPermission(Permission.BAN_MODERATOR))) {
                enableBannedUserTab();
            }
            if (!friendTabButton.isDisabled() && userManager.getInternUserView() == null) {
                friendTabButton.setDisabled(true);
            }
            if (!activeUserTabButton.isDisabled() && (userManager.getInternUserView() == null)
                || !userManager.getInternUserView().isInCurrentWorld()) {
                activeUserTabButton.setDisabled(true);
            }
            if (!bannedUserTabButton.isDisabled() && (userManager.getInternUserView() == null
                || !userManager.getInternUserView().hasPermission(Permission.BAN_USER)
                && !userManager.getInternUserView().hasPermission(Permission.BAN_MODERATOR))) {
                bannedUserTabButton.setDisabled(true);
            }
        }

         */
        super.draw(batch, parentAlpha);
    }

    private void enableBannedUserTab() {
    }

    private void enableActiveUserTab() {
    }

    private void enableFriendTab() {
    }

    @Override
    protected void create() {
        tabButtonGroup = new ButtonGroup<>();
        tabButtonGroup.setMinCheckCount(1);
        tabButtonGroup.setMaxCheckCount(1);
        tabButtonGroup.setUncheckLast(true);

        userListContainer = new Table(Chati.SKIN);
        userListScrollPane = new ScrollPane(userListContainer, Chati.SKIN);

        Skin friendTabButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        friendTabButton = new TextButton("Freunde", friendTabButtonSkin);
        friendTabButton.setStyle(HeadUpDisplay.disabledStyle);
        friendTabButton.getLabel().setColor(Color.DARK_GRAY);
        friendTabButton.setDisabled(true);

        Skin activeUserTabButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        activeUserTabButton = new TextButton("Welt", activeUserTabButtonSkin);
        activeUserTabButton.setStyle(HeadUpDisplay.disabledStyle);
        activeUserTabButton.getLabel().setColor(Color.DARK_GRAY);
        activeUserTabButton.setDisabled(true);

        Skin bannedUserTabButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        bannedUserTabButton = new TextButton("Gesperrt", bannedUserTabButtonSkin);
        bannedUserTabButton.setStyle(HeadUpDisplay.disabledStyle);
        bannedUserTabButton.getLabel().setColor(Color.DARK_GRAY);
        bannedUserTabButton.setDisabled(true);
    }

    @Override
    protected void setLayout() {
        top().right().padTop(HeadUpDisplay.BUTTON_SIZE);
        Window window = new Window("Benutzer", Chati.SKIN);
        window.setMovable(false);
        window.top();

        // TEST ///////////////////////////////////////////////////////////////////////////////////////////////////////
        String[] names = {"Jürgen", "Hans-Peter", "Detlef", "Olaf", "Markus", "Dietrich", "Dieter", "Siegbert", "Siegmund",
            "Joseph", "Ferdinand", "Alexander", "Adolf H", "Analia", "Inkontinentia", "Vagina", "Agathe", "Bertha", "Hannelore",
            "Sieglinde", "Josephine", "Brigitte", "Luise-Annegret", "Alma-Dorothea", "Magdalena", "Brunhilde", "Herbert",
            "Hagen", "Heinz", "Son-Goku", "Vegeta", "Axel Schweiß", "Rosa Schlüpfer", "Reinhold", "Mr.WasGehtSieDasAn", "Peter Enis",
            "Schwanzus-Longus", "G4meMason", "Franz Joseph", "Peter Silie", "Wilma Fiken", "Anna Bolika", "Anna Nass", "Deine Mutter"};
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
    }
}
