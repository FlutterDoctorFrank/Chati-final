package view2.component.hud.notificationList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.user.IInternUserView;
import model.user.IUserManagerView;
import view2.Assets;
import view2.Chati;
import view2.component.hud.HeadUpDisplay;
import view2.component.hud.HudMenuWindow;

import java.util.Set;
import java.util.TreeSet;

public class NotificationListWindow extends HudMenuWindow {

    private final Set<NotificationListEntry> globalNotificationEntries;
    private final Set<NotificationListEntry> worldNotificationEntries;

    private TextButton globalNotificationTabButton;
    private TextButton worldNotificationTabButton;
    private ScrollPane notificationListScrollPane;
    private Table notificationListContainer;

    public NotificationListWindow() {
        super("Benachrichtigungen");
        this.globalNotificationEntries = new TreeSet<>();
        this.worldNotificationEntries = new TreeSet<>();
        create();
        setLayout();
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
                selectButton(globalNotificationTabButton);
            } else if (user != null && user.isInCurrentWorld() && worldNotificationTabButton.isDisabled()) {
                enableButton(worldNotificationTabButton);
            }

            if (!globalNotificationTabButton.isDisabled() && globalNotificationTabButton.isChecked()) {
                showGlobalNotifications();
            } else if (!worldNotificationTabButton.isDisabled() && worldNotificationTabButton.isChecked()) {
                showWorldNotifications();
            }
        }

        super.act(delta);
    }

    @Override
    protected void create() {
        notificationListContainer = new Table();
        notificationListScrollPane = new ScrollPane(notificationListContainer, Assets.SKIN);

        globalNotificationTabButton = new TextButton("Global", Assets.getNewSkin());
        globalNotificationTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !globalNotificationTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showGlobalNotifications();
                selectButton(globalNotificationTabButton);
                if (!worldNotificationTabButton.isDisabled()) {
                    unselectButton(worldNotificationTabButton);
                }
            }
        });

        worldNotificationTabButton = new TextButton("Welt", Assets.getNewSkin());
        worldNotificationTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return !worldNotificationTabButton.isChecked();
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showWorldNotifications();
                selectButton(worldNotificationTabButton);
                if (!globalNotificationTabButton.isDisabled()) {
                    unselectButton(globalNotificationTabButton);
                }
            }
        });

        IInternUserView user = Chati.CHATI.getUserManager().getInternUserView();
        if (user == null) {
            disableGlobalNotificationTab();
            disableWorldNotificationTab();
        } else {
            selectButton(globalNotificationTabButton);
            if (!user.isInCurrentWorld()) {
                disableWorldNotificationTab();
            }

            showGlobalNotifications();
        }

        ButtonGroup<TextButton> tabButtonGroup = new ButtonGroup<>();
        tabButtonGroup.setMinCheckCount(1);
        tabButtonGroup.setMaxCheckCount(1);
        tabButtonGroup.setUncheckLast(true);
        tabButtonGroup.add(globalNotificationTabButton);
        tabButtonGroup.add(worldNotificationTabButton);
    }

    @Override
    protected void setLayout() {
        notificationListContainer.top();
        Table buttonContainer = new Table();
        buttonContainer.defaults().growX();
        buttonContainer.add(globalNotificationTabButton, worldNotificationTabButton);
        add(buttonContainer).growX().row();
        add(notificationListScrollPane).grow();
        Chati.CHATI.getScreen().getStage().setScrollFocus(notificationListScrollPane);
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
        entries.forEach(entry -> notificationListContainer.add(entry).fillX().expandX().row());
    }
}
