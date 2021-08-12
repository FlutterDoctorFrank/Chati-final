package view2.component.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import view2.Texture;
import view2.component.hud.notificationList.NotificationListTable;
import view2.component.hud.userList.UserListTable;

public class HeadUpDisplay extends Table {

    public static final float BUTTON_SCALE_FACTOR = 0.1f;
    public static final float BUTTON_SIZE = 75;
    public static final float BUTTON_SPACING = 10f;
    public static final float HUD_MENU_TABLE_WIDTH = 450;
    public static final float HUD_MENU_TABLE_HEIGHT = 600;
    public static final float HUD_MENU_TABLE_TAB_HEIGHT = 30;
    public static Button.ButtonStyle enabledStyle;
    public static Button.ButtonStyle disabledStyle;

    private Table currentListContainer;

    private ImageButton userListButton;
    private ImageButton notificationListButton;
    private ImageButton settingsButton;

    public HeadUpDisplay() {
        enabledStyle = new TextButton("", new Skin(Gdx.files.internal("shadeui/uiskin.json"))).getStyle();
        disabledStyle = new TextButton("", new Skin(Gdx.files.internal("shadeui/uiskin.json"))).getStyle();
        disabledStyle.up = disabledStyle.down;
        create();
        setLayout();
    }

    protected void create() {
        currentListContainer = new Table();
        currentListContainer.setFillParent(true);

        ButtonGroup<ImageButton> hudButtons = new ButtonGroup<>();
        hudButtons.setMinCheckCount(0);
        hudButtons.setMaxCheckCount(1);
        hudButtons.setUncheckLast(true);

        userListButton = new ImageButton(Texture.USER_ICON);
        userListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        userListButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (userListButton.isChecked()) {
                    userListButton.getStyle().imageUp = Texture.CHECKED_USER_ICON;
                    notificationListButton.getStyle().imageUp = Texture.NOTIFICATION_ICON;
                    settingsButton.getStyle().imageUp = Texture.SETTINGS_ICON;
                    currentListContainer.clearChildren();
                    currentListContainer.addActor(new UserListTable());
                } else {
                    userListButton.getStyle().imageUp = Texture.USER_ICON;
                    currentListContainer.clearChildren();
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    userListButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    userListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        notificationListButton = new ImageButton(Texture.NOTIFICATION_ICON);
        notificationListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        notificationListButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (notificationListButton.isChecked()) {
                    notificationListButton.getStyle().imageUp = Texture.CHECKED_NOTIFICATION_ICON;
                    userListButton.getStyle().imageUp = Texture.USER_ICON;
                    settingsButton.getStyle().imageUp = Texture.SETTINGS_ICON;
                    currentListContainer.clearChildren();
                    currentListContainer.addActor(new NotificationListTable());
                } else {
                    notificationListButton.getStyle().imageUp = Texture.NOTIFICATION_ICON;
                    currentListContainer.clearChildren();
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    notificationListButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    notificationListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        settingsButton = new ImageButton(Texture.SETTINGS_ICON);
        settingsButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        settingsButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (settingsButton.isChecked()) {
                    settingsButton.getStyle().imageUp = Texture.CHECKED_SETTINGS_ICON;
                    userListButton.getStyle().imageUp = Texture.USER_ICON;
                    notificationListButton.getStyle().imageUp = Texture.NOTIFICATION_ICON;
                    currentListContainer.clearChildren();
                } else {
                    settingsButton.getStyle().imageUp = Texture.SETTINGS_ICON;
                    currentListContainer.clearChildren();
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    settingsButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    settingsButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        hudButtons.add(userListButton);
        hudButtons.add(notificationListButton);
        hudButtons.add(settingsButton);
    }

    protected void setLayout() {
        setFillParent(true);
        Table container = new Table();
        container.setFillParent(true);
        container.top().right();
        container.add(userListButton).width(BUTTON_SIZE).height(BUTTON_SIZE).space(BUTTON_SPACING);
        container.add(notificationListButton).width(BUTTON_SIZE).height(BUTTON_SIZE).space(BUTTON_SPACING);
        container.add(settingsButton).width(BUTTON_SIZE).height(BUTTON_SIZE);
        addActor(container);

        addActor(currentListContainer);
    }
}
