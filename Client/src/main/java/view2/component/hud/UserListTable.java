package view2.component.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import model.role.Permission;
import model.user.IUserManagerView;
import model.user.IUserView;
import view2.Chati;
import view2.component.ChatiTable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserListTable extends HudMenuTable {

    private ButtonGroup<TextButton> tabButtonGroup;
    private TextButton activeUserTabButton;
    private TextButton friendTabButton;
    private TextButton bannedUserTabButton;

    private final Map<UUID, IUserView> activeUsers;
    private final Map<UUID, IUserView> friends;
    private final Map<UUID, IUserView> bannedUsers;

    protected UserListTable() {
        this.activeUsers = new HashMap<>();
        this.friends = new HashMap<>();
        this.bannedUsers = new HashMap<>();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (Chati.getInstance().isUserInfoChanged()) {
            IUserManagerView userManager = Chati.getInstance().getUserManager();
            if (userManager.getInternUserView() != null && userManager.getInternUserView().isInCurrentWorld()) {
                this.activeUsers.putAll(userManager.getActiveUsers());
            }
            this.friends.putAll(userManager.getFriends());
            if (userManager.getInternUserView() != null && (userManager.getInternUserView().hasPermission(Permission.BAN_USER)
                || userManager.getInternUserView().hasPermission(Permission.BAN_MODERATOR))) {
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

        activeUserTabButton = new TextButton("Aktive Benutzer", Chati.SKIN);
        activeUserTabButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }
        });

        friendTabButton = new TextButton("Freunde", Chati.SKIN);
        friendTabButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }
        });

        bannedUserTabButton = new TextButton("Gesperrte Benutzer", Chati.SKIN);
        friendTabButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }
        });
    }

    @Override
    protected void setLayout() {
        int width = 400;
        int height = 600;
        int buttonSize = 75;

        top().right().padTop(buttonSize).setFillParent(true);
        Window container = new Window("Benutzerliste", Chati.SKIN);
        container.setMovable(false);
        add(container).width(width).height(height);
    }
}
