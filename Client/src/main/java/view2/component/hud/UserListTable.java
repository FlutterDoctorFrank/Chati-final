package view2.component.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import model.role.Permission;
import model.user.IUserManagerView;
import model.user.IUserView;
import model.user.UserManager;
import view2.Chati;
import view2.component.ChatiTable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserListTable extends HudMenuTable {

    private static final String NAME = "user-list-table";

    private ButtonGroup<TextButton> tabButtonGroup;
    private HorizontalGroup tabHorizontalGroup;
    private TextButton activeUserTabButton;
    private TextButton friendTabButton;
    private TextButton bannedUserTabButton;

    private final Map<UUID, IUserView> activeUsers;
    private final Map<UUID, IUserView> friends;
    private final Map<UUID, IUserView> bannedUsers;

    protected UserListTable() {
        super(NAME);
        this.activeUsers = new HashMap<>();
        this.friends = new HashMap<>();
        this.bannedUsers = new HashMap<>();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (Chati.getInstance().isUserInfoChanged()) {
            IUserManagerView userManager = Chati.getInstance().getUserManager();
            this.activeUsers.putAll(userManager.getActiveUsers());
            this.friends.putAll(userManager.getFriends());
            if (userManager.getInternUserView().hasPermission(Permission.BAN_USER)
                || userManager.getInternUserView().hasPermission(Permission.BAN_MODERATOR)) {
                this.bannedUsers.putAll(userManager.getBannedUsers());
            }
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    protected void create() {
        tabButtonGroup = new ButtonGroup<>();
        tabButtonGroup.setMinCheckCount(0);
        tabButtonGroup.setMaxCheckCount(1);
        tabButtonGroup.setUncheckLast(true);

        activeUserTabButton = new TextButton("Aktive Benutzer", ChatiTable.SKIN);
        activeUserTabButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                activeUserTabButton.setChecked(true);
            }
        });
    }

    @Override
    protected void setLayout() {

    }
}
