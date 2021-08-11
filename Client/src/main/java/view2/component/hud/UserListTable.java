package view2.component.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import model.role.Permission;
import model.user.IUserManagerView;
import model.user.IUserView;
import view2.Chati;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserListTable extends HudMenuTable {

    private ButtonGroup<TextButton> tabButtonGroup;
    private TextButton friendTabButton;
    private TextButton activeUserTabButton;
    private TextButton bannedUserTabButton;

    private final Map<UUID, IUserView> friends;
    private final Map<UUID, IUserView> activeUsers;
    private final Map<UUID, IUserView> bannedUsers;

    protected UserListTable() {
        this.friends = new HashMap<>();
        this.activeUsers = new HashMap<>();
        this.bannedUsers = new HashMap<>();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
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
        }
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
        tabButtonGroup.setMinCheckCount(0);
        tabButtonGroup.setMaxCheckCount(1);
        tabButtonGroup.setUncheckLast(true);

        Skin friendTabButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        friendTabButton = new TextButton("Freunde", friendTabButtonSkin);
        friendTabButton.setStyle(HeadUpDisplay.disabledStyle);
        friendTabButton.setDisabled(true);

        Skin activeUserTabButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        activeUserTabButton = new TextButton("Aktive Benutzer", activeUserTabButtonSkin);
        activeUserTabButton.setStyle(HeadUpDisplay.disabledStyle);
        activeUserTabButton.setDisabled(true);

        Skin bannedUserTabButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        bannedUserTabButton = new TextButton("Gesperrte Benutzer", bannedUserTabButtonSkin);
        bannedUserTabButton.setStyle(HeadUpDisplay.disabledStyle);
        bannedUserTabButton.setDisabled(true);
    }

    @Override
    protected void setLayout() {
        top().right().padTop(HeadUpDisplay.BUTTON_SIZE).setFillParent(true);
        Window container = new Window("Benutzerliste", Chati.SKIN);
        container.setMovable(false);
        add(container).width(HeadUpDisplay.HUD_MENU_TABLE_WIDTH).height(HeadUpDisplay.HUD_MENU_TABLE_HEIGHT);
    }
}
