package view.UIComponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class DropDownMenu extends HorizontalGroup {
    private DropDownMenuButton settings;
    private DropDownMenuButton notifications;
    private DropDownMenuButton users;
    private Hud hud;
    private DropDownMenuButton currentButtonPressed;

    public DropDownMenu(Hud hud) {
        this.hud = hud;
        setHeight(35);
        setWidth(35);
        create();
        setName("drop-down-menus");
        top().right();
        setFillParent(true);
    }

    private void create() {
        this.settings = new DropDownMenuButton(new Texture("icons/settings_img.png"), "settings", new SettingsTable(this));
        this.notifications = new DropDownMenuButton(new Texture("icons/speech_bubble_img.png"), "notifications", new NotificationsTable(this));
        this.users = new DropDownMenuButton(new Texture("icons/user_img.png"), "users", new UsersListTable(this));

        addActor(notifications);
        addActor(users);
        addActor(settings);
    }

    public DropDownMenuButton getCurrentButtonPressed() {
        return currentButtonPressed;
    }

    public void setCurrentButtonPressed(DropDownMenuButton currentButtonPressed) {
        this.currentButtonPressed = currentButtonPressed;

    }

    public Hud getHud() {
        return hud;
    }
}