package view2.component.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
        top().right().padTop(HeadUpDisplay.BUTTON_SIZE).setFillParent(true);
        Window window = new Window("Benutzerliste", Chati.SKIN);
        window.setMovable(false);
        window.top();
        window.add(friendTabButton).width((HeadUpDisplay.HUD_MENU_TABLE_WIDTH - window.getPadX()) / 3).height(HeadUpDisplay.HUD_MENU_TABLE_TAB_HEIGHT);
        window.add(activeUserTabButton).width((HeadUpDisplay.HUD_MENU_TABLE_WIDTH - window.getPadX()) / 3).height(HeadUpDisplay.HUD_MENU_TABLE_TAB_HEIGHT);
        window.add(bannedUserTabButton).width((HeadUpDisplay.HUD_MENU_TABLE_WIDTH - window.getPadX()) / 3).height(HeadUpDisplay.HUD_MENU_TABLE_TAB_HEIGHT);
        add(window).width(HeadUpDisplay.HUD_MENU_TABLE_WIDTH).height(HeadUpDisplay.HUD_MENU_TABLE_HEIGHT);
    }
}
