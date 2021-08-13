package view2.component.hud.notificationList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.MessageBundle;
import model.notification.Notification;
import model.notification.NotificationType;
import model.user.IInternUserView;
import model.user.IUserManagerView;
import view2.Chati;
import view2.component.ChatiTable;
import view2.component.hud.HeadUpDisplay;

import java.time.LocalDateTime;
import java.util.*;

public class NotificationListTable extends ChatiTable {

    private final Set<NotificationListEntry> globalNotificationEntries;
    private final Set<NotificationListEntry> worldNotificationEntries;

    private ButtonGroup<TextButton> tabButtonGroup;
    private TextButton globalNotificationTabButton;
    private TextButton worldNotificationTabButton;
    private ScrollPane notificationListScrollPane;
    private Table notificationListContainer;

    public NotificationListTable() {
        this.globalNotificationEntries = new TreeSet<>();
        this.worldNotificationEntries = new TreeSet<>();
        create();
        setLayout();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        IInternUserView user = Chati.getInstance().getUserManager().getInternUserView();
        if (user == null && !globalNotificationTabButton.isDisabled()) {
            disableWorldNotificationTab();
            disableGlobalNotificationTab();
        } else if (user != null && !user.isInCurrentWorld() && !worldNotificationTabButton.isDisabled()) {
            disableWorldNotificationTab();
        }

        if (user != null && globalNotificationTabButton.isDisabled()) {
            enableGlobalNotificationTab();
            showGlobalNotifications();
        } else if (user != null && user.isInCurrentWorld() && worldNotificationTabButton.isDisabled()) {
            enableWorldNotificationTab();
        }

        if (Chati.getInstance().isUserNotificationChanged()) {
            if (!globalNotificationTabButton.isDisabled() && globalNotificationTabButton.isChecked()) {
                showGlobalNotifications();
            } else if (!worldNotificationTabButton.isDisabled() && worldNotificationTabButton.isChecked()) {
                showWorldNotifications();
            }
        }
        super.draw(batch, parentAlpha);
    }

    protected void create() {
        tabButtonGroup = new ButtonGroup<>();
        tabButtonGroup.setMinCheckCount(1);
        tabButtonGroup.setMaxCheckCount(1);
        tabButtonGroup.setUncheckLast(true);

        notificationListContainer = new Table(Chati.SKIN);
        notificationListScrollPane = new ScrollPane(notificationListContainer, Chati.SKIN);

        Skin globalNotificationTabButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        globalNotificationTabButton = new TextButton("Global", globalNotificationTabButtonSkin);
        globalNotificationTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !globalNotificationTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showGlobalNotifications();
                globalNotificationTabButton.getLabel().setColor(Color.MAGENTA);
                globalNotificationTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
                if (!worldNotificationTabButton.isDisabled()) {
                    worldNotificationTabButton.getLabel().setColor(Color.WHITE);
                    worldNotificationTabButton.getStyle().up = HeadUpDisplay.UNPRESSED_BUTTON_IMAGE;
                }
            }
        });

        Skin worldNotificationTabButtonSkin = new Skin(Gdx.files.internal("shadeui/uiskin.json"));
        worldNotificationTabButton = new TextButton("Welt", worldNotificationTabButtonSkin);
        worldNotificationTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !worldNotificationTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showWorldNotifications();
                worldNotificationTabButton.getLabel().setColor(Color.MAGENTA);
                worldNotificationTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
                globalNotificationTabButton.getLabel().setColor(Color.WHITE);
                globalNotificationTabButton.getStyle().up = HeadUpDisplay.UNPRESSED_BUTTON_IMAGE;
            }
        });

        tabButtonGroup.add(globalNotificationTabButton);
        tabButtonGroup.add(worldNotificationTabButton);

        IInternUserView user = Chati.getInstance().getUserManager().getInternUserView();
        if (user == null) {
            disableGlobalNotificationTab();
            disableWorldNotificationTab();
        } else {
            globalNotificationTabButton.getLabel().setColor(Color.MAGENTA);
            globalNotificationTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
            if (!user.isInCurrentWorld()) {
                disableWorldNotificationTab();
            }

            showGlobalNotifications();
        }
    }

    protected void setLayout() {
        top().right().padTop(HeadUpDisplay.BUTTON_SIZE);
        Window window = new Window("Benachrichtigungen", Chati.SKIN);
        window.setMovable(false);
        window.top();

        notificationListContainer.top();

        Table buttonContainer = new Table(Chati.SKIN);
        buttonContainer.add(globalNotificationTabButton).fillX().expandX();
        buttonContainer.add(worldNotificationTabButton).fillX().expandX();
        window.add(buttonContainer).fillX().expandX().row();

        window.add(notificationListScrollPane).fillX().expandX().fillY().expandY();
        add(window).width(HeadUpDisplay.HUD_MENU_TABLE_WIDTH).height(HeadUpDisplay.HUD_MENU_TABLE_HEIGHT);

        Chati.getInstance().getMenuScreen().getStage().setScrollFocus(notificationListScrollPane);
    }

    private void showGlobalNotifications() {
        globalNotificationEntries.clear();
        IUserManagerView userManager = Chati.getInstance().getUserManager();
        if (userManager.isLoggedIn() && userManager.getInternUserView() != null) {
            userManager.getInternUserView().getGlobalNotifications().values()
                    .forEach(notification -> globalNotificationEntries.add(new NotificationListEntry(notification)));
            layoutEntries(globalNotificationEntries);
        }
/*
        // TEEEEESST //
        for (int i = 0; i <20; i++) {
            Notification notification = new Notification(UUID.randomUUID(), null, new MessageBundle("Ich bin ein globaler Nachricht"), LocalDateTime.now(),
                    NotificationType.values()[new Random().nextInt(NotificationType.values().length)]);
            globalNotificationEntries.add(new NotificationListEntry(notification));
        }
        layoutEntries(globalNotificationEntries);
        // TEEESST ENDE //

 */
    }

    private void showWorldNotifications() {
        /*
        worldNotificationEntries.clear();
        IInternUserView internUser = Chati.getInstance().getUserManager().getInternUserView();
        if (internUser != null && internUser.isInCurrentWorld()) {
            internUser.getWorldNotifications().values()
                    .forEach(notification -> worldNotificationEntries.add(new NotificationListEntry(notification)));
            layoutEntries(worldNotificationEntries);
        }
         */

        // TEEEEEST   //
        for (int i = 0; i <20; i++) {
            Notification notification = new Notification(UUID.randomUUID(), null, new MessageBundle("Ich bin eine Weltnachricht"), LocalDateTime.now(),
                    NotificationType.values()[new Random().nextInt(NotificationType.values().length)]);
            worldNotificationEntries.add(new NotificationListEntry(notification));
        }
        layoutEntries(worldNotificationEntries);
        // TEEESST ENDE //
    }

    private void enableGlobalNotificationTab() {
        globalNotificationTabButton.setDisabled(false);
        globalNotificationTabButton.setTouchable(Touchable.enabled);
        globalNotificationTabButton.getLabel().setColor(Color.MAGENTA);
        globalNotificationTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
    }

    private void enableWorldNotificationTab() {
        worldNotificationTabButton.setDisabled(false);
        worldNotificationTabButton.setTouchable(Touchable.enabled);
        worldNotificationTabButton.getLabel().setColor(Color.WHITE);
        worldNotificationTabButton.getStyle().up = HeadUpDisplay.UNPRESSED_BUTTON_IMAGE;
    }

    private void disableGlobalNotificationTab() {
        globalNotificationTabButton.setDisabled(true);
        globalNotificationTabButton.setTouchable(Touchable.disabled);
        globalNotificationTabButton.getLabel().setColor(Color.DARK_GRAY);
        globalNotificationTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
        globalNotificationEntries.clear();
        notificationListContainer.clearChildren();
    }

    private void disableWorldNotificationTab() {
        if (worldNotificationTabButton.isChecked() && !globalNotificationTabButton.isDisabled()) {
            worldNotificationTabButton.setChecked(false);
            globalNotificationTabButton.setChecked(true);
            showGlobalNotifications();
        }
        worldNotificationTabButton.setDisabled(true);
        worldNotificationTabButton.setTouchable(Touchable.disabled);
        worldNotificationTabButton.getLabel().setColor(Color.DARK_GRAY);
        worldNotificationTabButton.getStyle().up = HeadUpDisplay.PRESSED_BUTTON_IMAGE;
        worldNotificationEntries.clear();
    }

    private void layoutEntries(Set<NotificationListEntry> entries) {
        notificationListContainer.clearChildren();
        entries.forEach(entry -> notificationListContainer.add(entry).fillX().expandX().row());
    }
}
