package view.UIComponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class DropDownMenus extends HorizontalGroup {
    private DropDownMenuButton settings;
    private DropDownMenuButton notifications;
    private DropDownMenuButton users;
    private Hud hud;
    private String currentButtonPressed;

    public DropDownMenus (Hud hud) {
        this.hud = hud;
        create();
        setName("drop-down-menus");
        top().right();
        setFillParent(true);
    }

    private void create() {
        this.settings = new DropDownMenuButton(new Texture("icons/settings_img.png"), "settings", new SettingsTable(hud), hud, this);
        this.notifications = new DropDownMenuButton(new Texture("icons/speech_bubble_img.png"), "notifications", new NotificationsTable(hud), hud, this);
        this.users = new DropDownMenuButton(new Texture("icons/user_img.png"), "users", new UsersListTable(hud), hud, this);

        addActor(notifications);
        addActor(users);
        addActor(settings);
    }

    public String getCurrentButtonPressed() {
        return currentButtonPressed;
    }

    public void setCurrentButtonPressed(String currentButtonPressed) {
        this.currentButtonPressed = currentButtonPressed;
    }
}
