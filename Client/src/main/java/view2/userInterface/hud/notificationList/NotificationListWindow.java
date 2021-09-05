package view2.userInterface.hud.notificationList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.user.IInternUserView;
import model.user.IUserManagerView;
import view2.Chati;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.hud.HudMenuWindow;

import java.util.Set;
import java.util.TreeSet;

public class NotificationListWindow extends HudMenuWindow {

    private final Set<NotificationListEntry> globalNotificationEntries;
    private final Set<NotificationListEntry> worldNotificationEntries;

    private final ChatiTextButton globalNotificationTabButton;
    private final ChatiTextButton worldNotificationTabButton;
    private final Table notificationListContainer;

    public NotificationListWindow() {
        super("Benachrichtigungen");
        this.globalNotificationEntries = new TreeSet<>();
        this.worldNotificationEntries = new TreeSet<>();

        notificationListContainer = new Table();
        ScrollPane notificationListScrollPane = new ScrollPane(notificationListContainer, Chati.CHATI.getSkin());

        globalNotificationTabButton = new ChatiTextButton("Global", false);
        globalNotificationTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !globalNotificationTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showGlobalNotifications();
                globalNotificationTabButton.setChecked(true);
                if (!worldNotificationTabButton.isDisabled()) {
                    worldNotificationTabButton.setChecked(false);
                }
            }
        });

        worldNotificationTabButton = new ChatiTextButton("Welt", false);
        worldNotificationTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !worldNotificationTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showWorldNotifications();
                worldNotificationTabButton.setChecked(true);
                if (!globalNotificationTabButton.isDisabled()) {
                    globalNotificationTabButton.setChecked(false);
                }
            }
        });

        ButtonGroup<TextButton> tabButtonGroup = new ButtonGroup<>();
        tabButtonGroup.setMinCheckCount(1);
        tabButtonGroup.setMaxCheckCount(1);
        tabButtonGroup.setUncheckLast(true);
        tabButtonGroup.add(globalNotificationTabButton);
        tabButtonGroup.add(worldNotificationTabButton);

        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser == null) {
            disableGlobalNotificationTab();
            disableWorldNotificationTab();
        } else {
            globalNotificationTabButton.setChecked(true);
            if (!internUser.isInCurrentWorld()) {
                disableWorldNotificationTab();
            }

            showGlobalNotifications();
        }

        // Layout
        notificationListContainer.top();
        Table buttonContainer = new Table();
        buttonContainer.defaults().growX();
        buttonContainer.add(globalNotificationTabButton, worldNotificationTabButton);
        add(buttonContainer).growX().row();
        add(notificationListScrollPane).grow();
        Chati.CHATI.getScreen().getStage().setScrollFocus(notificationListScrollPane);
    }

    @Override
    public void act(float delta) {
        if (Chati.CHATI.isUserNotificationChanged() || Chati.CHATI.isWorldChanged()) {
            IInternUserView user = Chati.CHATI.getUserManager().getInternUserView();
            if (user == null && !globalNotificationTabButton.isDisabled()) {
                disableWorldNotificationTab();
                disableGlobalNotificationTab();
            } else if (user != null && !user.isInCurrentWorld() && !worldNotificationTabButton.isDisabled()) {
                disableWorldNotificationTab();
            }

            if (user != null && globalNotificationTabButton.isDisabled()) {
                enableButton(globalNotificationTabButton);
                globalNotificationTabButton.setChecked(true);
            } else if (user != null && user.isInCurrentWorld() && worldNotificationTabButton.isDisabled()) {
                worldNotificationTabButton.setChecked(true);
            }

            if (!globalNotificationTabButton.isDisabled() && globalNotificationTabButton.isChecked()) {
                showGlobalNotifications();
            } else if (!worldNotificationTabButton.isDisabled() && worldNotificationTabButton.isChecked()) {
                showWorldNotifications();
            }
        }

        super.act(delta);
    }

    private void showGlobalNotifications() {
        globalNotificationEntries.clear();
        IUserManagerView userManager = Chati.CHATI.getUserManager();
        if (userManager.isLoggedIn() && userManager.getInternUserView() != null) {
            userManager.getInternUserView().getGlobalNotifications().values()
                    .forEach(notification -> globalNotificationEntries.add(new NotificationListEntry(notification)));
            layoutEntries(globalNotificationEntries);
        }
    }

    private void showWorldNotifications() {
        worldNotificationEntries.clear();
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
        if (internUser != null && internUser.isInCurrentWorld()) {
            internUser.getWorldNotifications().values()
                    .forEach(notification -> worldNotificationEntries.add(new NotificationListEntry(notification)));
            layoutEntries(worldNotificationEntries);
        }
    }

    private void disableGlobalNotificationTab() {
        disableButton(globalNotificationTabButton);
        globalNotificationEntries.clear();
        notificationListContainer.clearChildren();
    }

    private void disableWorldNotificationTab() {
        if (worldNotificationTabButton.isChecked() && !globalNotificationTabButton.isDisabled()) {
            worldNotificationTabButton.setChecked(false);
            globalNotificationTabButton.setChecked(true);
            showGlobalNotifications();
        }
        disableButton(worldNotificationTabButton);
        worldNotificationEntries.clear();
    }

    private void layoutEntries(Set<NotificationListEntry> entries) {
        notificationListContainer.clearChildren();
        entries.forEach(entry -> notificationListContainer.add(entry).growX().row());
    }
}
