package view2.component.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import view2.Chati;
import view2.Texture;
import view2.component.hud.internUserDisplay.InternUserDisplay;
import view2.component.hud.notificationList.NotificationListTable;
import view2.component.hud.settings.SettingsTable;
import view2.component.hud.userList.UserListTable;

public class HeadUpDisplay extends Table {

    public static final float BUTTON_SCALE_FACTOR = 0.1f;
    public static final float BUTTON_SIZE = 75;
    public static final float BUTTON_SPACING = 10f;
    public static final float HUD_MENU_TABLE_WIDTH = 450;
    public static final float HUD_MENU_TABLE_HEIGHT = 600;

    public static HeadUpDisplay headUpDisplay;

    private Table internUserDisplayContainer;
    private Table currentMenuContainer;

    private ImageButton userListButton;
    private ImageButton notificationListButton;
    private ImageButton settingsButton;

    private HeadUpDisplay() {
        create();
        setLayout();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (Chati.getInstance().getUserManager().getInternUserView() != null
                && internUserDisplayContainer.getChildren().isEmpty()) {
            internUserDisplayContainer.add(new InternUserDisplay()).width(HUD_MENU_TABLE_WIDTH).height(HUD_MENU_TABLE_HEIGHT);
        } else if (Chati.getInstance().getUserManager().getInternUserView() == null
                && !internUserDisplayContainer.getChildren().isEmpty()) {
            internUserDisplayContainer.clearChildren();
        }
        super.draw(batch, parentAlpha);
    }

    protected void create() {
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
                    currentMenuContainer.clearChildren();
                    currentMenuContainer.addActor(new UserListTable());
                } else {
                    userListButton.getStyle().imageUp = Texture.USER_ICON;
                    currentMenuContainer.clearChildren();
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
                    currentMenuContainer.clearChildren();
                    currentMenuContainer.addActor(new NotificationListTable());
                } else {
                    notificationListButton.getStyle().imageUp = Texture.NOTIFICATION_ICON;
                    currentMenuContainer.clearChildren();
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
                    currentMenuContainer.clearChildren();
                    currentMenuContainer.addActor(new SettingsTable());
                } else {
                    settingsButton.getStyle().imageUp = Texture.SETTINGS_ICON;
                    currentMenuContainer.clearChildren();
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

        Table buttonContainer = new Table();
        buttonContainer.setFillParent(true);
        buttonContainer.top().right();
        buttonContainer.add(userListButton).width(BUTTON_SIZE).height(BUTTON_SIZE).space(BUTTON_SPACING);
        buttonContainer.add(notificationListButton).width(BUTTON_SIZE).height(BUTTON_SIZE).space(BUTTON_SPACING);
        buttonContainer.add(settingsButton).width(BUTTON_SIZE).height(BUTTON_SIZE);
        addActor(buttonContainer);

        currentMenuContainer = new Table();
        currentMenuContainer.setFillParent(true);
        currentMenuContainer.top().right().padTop(BUTTON_SIZE);
        currentMenuContainer.defaults().width(HUD_MENU_TABLE_WIDTH).height(HUD_MENU_TABLE_HEIGHT);
        addActor(currentMenuContainer);

        internUserDisplayContainer = new Table();
        internUserDisplayContainer.setFillParent(true);
        internUserDisplayContainer.top().left();
        internUserDisplayContainer.defaults().width(HUD_MENU_TABLE_WIDTH).height(HUD_MENU_TABLE_HEIGHT);
        addActor(internUserDisplayContainer);
    }

    public static HeadUpDisplay getInstance() {
        if (headUpDisplay == null) {
            headUpDisplay = new HeadUpDisplay();
        }
        return headUpDisplay;
    }
}
