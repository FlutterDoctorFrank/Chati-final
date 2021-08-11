package view2.component.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class HeadUpDisplay extends Stage {

    private static final String USER_ICON_PATH = "icons/userIcon.png";
    private static final String USER_ICON_CHECKED_PATH = "icons/userIconChecked.png";
    private static final String NOTIFICATION_ICON_PATH = "icons/notificationIcon.png";
    private static final String NOTIFICATION_ICON_CHECKED_PATH = "icons/notificationIconChecked.png";
    private static final String SETTINGS_ICON_PATH = "icons/settingsIcon.png";
    private static final String SETTINGS_ICON_CHECKED_PATH = "icons/settingsIconChecked.png";
    static final float BUTTON_SCALE_FACTOR = 0.1f;
    static final float BUTTON_SIZE = 75;
    static final float BUTTON_SPACING = 2.5f;
    static final float HUD_MENU_TABLE_WIDTH = 400;
    static final float HUD_MENU_TABLE_HEIGHT = 600;
    static Button.ButtonStyle enabledStyle;
    static Button.ButtonStyle disabledStyle;

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

        TextureRegionDrawable userIcon = new TextureRegionDrawable(new TextureRegion(new Texture(USER_ICON_PATH)));
        TextureRegionDrawable userIconChecked = new TextureRegionDrawable(new TextureRegion(new Texture(USER_ICON_CHECKED_PATH)));
        TextureRegionDrawable notificationIcon = new TextureRegionDrawable(new TextureRegion(new Texture(NOTIFICATION_ICON_PATH)));
        TextureRegionDrawable notificationIconChecked = new TextureRegionDrawable(new TextureRegion(new Texture(NOTIFICATION_ICON_CHECKED_PATH)));
        TextureRegionDrawable settingsIcon = new TextureRegionDrawable(new TextureRegion(new Texture(SETTINGS_ICON_PATH)));
        TextureRegionDrawable settingsIconChecked = new TextureRegionDrawable(new TextureRegion(new Texture(SETTINGS_ICON_CHECKED_PATH)));

        ButtonGroup<ImageButton> hudButtons = new ButtonGroup<>();
        hudButtons.setMinCheckCount(0);
        hudButtons.setMaxCheckCount(1);
        hudButtons.setUncheckLast(true);

        userListButton = new ImageButton(userIcon);
        userListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        userListButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (userListButton.isChecked()) {
                    userListButton.getStyle().imageUp = userIconChecked;
                    notificationListButton.getStyle().imageUp = notificationIcon;
                    settingsButton.getStyle().imageUp = settingsIcon;
                    currentListContainer.clearChildren();
                    currentListContainer.addActor(new UserListTable());
                } else {
                    userListButton.getStyle().imageUp = userIcon;
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

        notificationListButton = new ImageButton(notificationIcon);
        notificationListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        notificationListButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (notificationListButton.isChecked()) {
                    notificationListButton.getStyle().imageUp = notificationIconChecked;
                    userListButton.getStyle().imageUp = userIcon;
                    settingsButton.getStyle().imageUp = settingsIcon;
                    currentListContainer.clearChildren();
                } else {
                    notificationListButton.getStyle().imageUp = notificationIcon;
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

        settingsButton = new ImageButton(settingsIcon);
        settingsButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        settingsButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (settingsButton.isChecked()) {
                    settingsButton.getStyle().imageUp = settingsIconChecked;
                    userListButton.getStyle().imageUp = userIcon;
                    notificationListButton.getStyle().imageUp = notificationIcon;
                    currentListContainer.clearChildren();
                } else {
                    settingsButton.getStyle().imageUp = settingsIcon;
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
