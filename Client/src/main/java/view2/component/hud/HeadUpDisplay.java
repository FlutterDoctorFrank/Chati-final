package view2.component.hud;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.communication.message.MessageType;
import model.user.IInternUserView;
import view2.Chati;
import view2.Icon;
import view2.component.hud.internUserDisplay.InternUserDisplay;
import view2.component.hud.notificationList.NotificationListTable;
import view2.component.hud.settings.SettingsTable;
import view2.component.hud.userList.UserListTable;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class HeadUpDisplay extends Table {

    public static final float BUTTON_SCALE_FACTOR = 0.1f;
    public static final float BUTTON_SIZE = 75;
    public static final float BUTTON_SPACING = 10f;
    public static final float HUD_MENU_TABLE_WIDTH = 450;
    public static final float HUD_MENU_TABLE_HEIGHT = 600;

    private static HeadUpDisplay headUpDisplay;

    private Table internUserDisplayContainer;
    private Table currentMenuContainer;

    private ImageButton userListButton;
    private ImageButton notificationListButton;
    private ImageButton settingsButton;
    private ImageButton chatButton;

    private HeadUpDisplay() {
        create();
        setLayout();
    }

    @Override
    public void act(float delta) {
        if (Chati.getInstance().isUserInfoChanged() || Chati.getInstance().isWorldChanged()) {
            IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
            if (internUser != null && internUserDisplayContainer.getChildren().isEmpty()) {
                internUserDisplayContainer.add(new InternUserDisplay()).width(HUD_MENU_TABLE_WIDTH).height(HUD_MENU_TABLE_HEIGHT);
            } else if (internUser == null && !internUserDisplayContainer.getChildren().isEmpty()) {
                internUserDisplayContainer.clearChildren();
            }
            if (internUser != null && internUser.isInCurrentWorld() && !chatButton.isVisible()) {
                chatButton.setVisible(true);
            } else if ((internUser == null || !internUser.isInCurrentWorld()) && chatButton.isVisible()) {
                ChatWindow.getInstance().clearChat();
                hideChatWindow();
                chatButton.setVisible(false);
            }
        }
        super.act(delta);
    }

    protected void create() {
        userListButton = new ImageButton(Icon.USER_ICON);
        userListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        userListButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (userListButton.isChecked()) {
                    openUserMenu();
                } else {
                    closeMenus();
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

        notificationListButton = new ImageButton(Icon.NOTIFICATION_ICON);
        notificationListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        notificationListButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (notificationListButton.isChecked()) {
                    openNotificationMenu();
                } else {
                    closeMenus();
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

        settingsButton = new ImageButton(Icon.SETTINGS_ICON);
        settingsButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        settingsButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (settingsButton.isChecked()) {
                    openSettingsMenu();
                } else {
                    closeMenus();
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

        chatButton = new ImageButton(Icon.CHAT_ICON);
        chatButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
        chatButton.setVisible(false);
        chatButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (chatButton.isChecked()) {
                    showChatWindow();
                } else {
                    hideChatWindow();
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    chatButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    chatButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        ButtonGroup<ImageButton> hudButtons = new ButtonGroup<>();
        hudButtons.setMinCheckCount(0);
        hudButtons.setMaxCheckCount(1);
        hudButtons.setUncheckLast(true);
        hudButtons.add(userListButton);
        hudButtons.add(notificationListButton);
        hudButtons.add(settingsButton);
    }

    private void setLayout() {
        setFillParent(true);

        Table hudButtonContainer = new Table();
        hudButtonContainer.setFillParent(true);
        hudButtonContainer.top().right();
        hudButtonContainer.add(userListButton).width(BUTTON_SIZE).height(BUTTON_SIZE).space(BUTTON_SPACING);
        hudButtonContainer.add(notificationListButton).width(BUTTON_SIZE).height(BUTTON_SIZE).space(BUTTON_SPACING);
        hudButtonContainer.add(settingsButton).width(BUTTON_SIZE).height(BUTTON_SIZE);
        addActor(hudButtonContainer);

        Table chatButtonContainer = new Table();
        chatButtonContainer.setFillParent(true);
        chatButtonContainer.bottom().right();
        chatButtonContainer.add(chatButton).width(BUTTON_SIZE).height(BUTTON_SIZE);
        addActor(chatButtonContainer);

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

    public void showChatWindow() {
        chatButton.setChecked(true);
        chatButton.getStyle().imageUp = Icon.CHECKED_CHAT_ICON;
        ChatWindow.getInstance().setVisible(true);

        //////////////////////// TEST ////////////////////////
        for (int i = 0; i <10; i++) {
            UUID id = Chati.getInstance().getUserManager().getInternUserView().getUserId();
            String message = "uihferuwihfieeraofhvueifhuiergfbrgfiqeowjderniogwuihfosdhvwrbfhdsvbworgbuiaeofjdhioh0";
            MessageType type = MessageType.values()[new Random().nextInt(MessageType.values().length)];
            LocalDateTime stamp = LocalDateTime.now();
            ChatWindow.getInstance().showMessage(id, message, type, stamp);
        }
        ////////////////// TEST ENDE ///////////////////////////
    }

    public void hideChatWindow() {
        chatButton.setChecked(false);
        chatButton.getStyle().imageUp = Icon.CHAT_ICON;
        ChatWindow.getInstance().setVisible(false);
        getStage().unfocus(ChatWindow.getInstance());
    }

    public boolean isChatEnabled() {
        return chatButton.isVisible();
    }

    public static HeadUpDisplay getInstance() {
        if (headUpDisplay == null) {
            headUpDisplay = new HeadUpDisplay();
        }
        return headUpDisplay;
    }

    public boolean isUserMenuOpen() {
        return userListButton.isChecked();
    }

    public boolean isNotificationMenuOpen() {
        return notificationListButton.isChecked();
    }

    public boolean isSettingsMenuOpen() {
        return settingsButton.isChecked();
    }

    public void openUserMenu() {
        userListButton.setChecked(true);
        userListButton.getStyle().imageUp = Icon.CHECKED_USER_ICON;
        notificationListButton.getStyle().imageUp = Icon.NOTIFICATION_ICON;
        settingsButton.getStyle().imageUp = Icon.SETTINGS_ICON;
        currentMenuContainer.clearChildren();
        currentMenuContainer.addActor(new UserListTable());
    }

    public void openNotificationMenu() {
        notificationListButton.setChecked(true);
        notificationListButton.getStyle().imageUp = Icon.CHECKED_NOTIFICATION_ICON;
        userListButton.getStyle().imageUp = Icon.USER_ICON;
        settingsButton.getStyle().imageUp = Icon.SETTINGS_ICON;
        currentMenuContainer.clearChildren();
        currentMenuContainer.addActor(new NotificationListTable());
    }

    public void openSettingsMenu() {
        settingsButton.setChecked(true);
        settingsButton.getStyle().imageUp = Icon.CHECKED_SETTINGS_ICON;
        userListButton.getStyle().imageUp = Icon.USER_ICON;
        notificationListButton.getStyle().imageUp = Icon.NOTIFICATION_ICON;
        currentMenuContainer.clearChildren();
        currentMenuContainer.addActor(new SettingsTable());
    }

    public void closeMenus() {
        userListButton.setChecked(false);
        notificationListButton.setChecked(false);
        settingsButton.setChecked(false);
        userListButton.getStyle().imageUp = Icon.USER_ICON;
        notificationListButton.getStyle().imageUp = Icon.NOTIFICATION_ICON;
        settingsButton.getStyle().imageUp = Icon.SETTINGS_ICON;
        currentMenuContainer.clearChildren();
    }
}
