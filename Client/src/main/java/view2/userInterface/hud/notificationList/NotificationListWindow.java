package view2.userInterface.hud.notificationList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import model.user.IInternUserView;
import model.user.IUserManagerView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.hud.HudMenuWindow;
import java.util.Set;
import java.util.TreeSet;

/**
 * Eine Klasse, welche das Benachrichtigungsmenü des HeadUpDisplay repräsentiert.
 */
public class NotificationListWindow extends HudMenuWindow {

    private final Set<NotificationListEntry> globalNotificationEntries;
    private final Set<NotificationListEntry> worldNotificationEntries;

    private final ChatiTextButton globalNotificationTabButton;
    private final ChatiTextButton worldNotificationTabButton;
    private final ScrollPane notificationListScrollPane;
    private final Table notificationListContainer;

    /**
     * Erzeugt eine neue Instanz des NotificationListWindow.
     */
    public NotificationListWindow() {
        super("window.title.notifications");
        this.globalNotificationEntries = new TreeSet<>();
        this.worldNotificationEntries = new TreeSet<>();

        notificationListContainer = new Table();
        notificationListScrollPane = new ScrollPane(notificationListContainer, Chati.CHATI.getSkin());
        notificationListScrollPane.setFadeScrollBars(false);
        notificationListScrollPane.setOverscroll(false, false);
        notificationListScrollPane.setScrollingDisabled(true, false);

        globalNotificationTabButton = new ChatiTextButton("menu.button.global", false);
        globalNotificationTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(@NotNull final InputEvent event, final float x, final float y,
                                     final int pointer, final int button) {
                return !globalNotificationTabButton.isChecked();
            }
            @Override
            public void touchUp(@NotNull final InputEvent event, final float x, final float y,
                                final int pointer, final int button) {
                showGlobalNotifications();
                globalNotificationTabButton.setChecked(true);
                if (!worldNotificationTabButton.isDisabled()) {
                    worldNotificationTabButton.setChecked(false);
                }
            }
        });

        worldNotificationTabButton = new ChatiTextButton("menu.button.world", false);
        worldNotificationTabButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(@NotNull final InputEvent event, final float x, final float y,
                                     final int pointer, final int button) {
                return !worldNotificationTabButton.isChecked();
            }
            @Override
            public void touchUp(@NotNull final InputEvent event, final float x, final float y,
                                final int pointer, final int button) {
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

        // Translatable register
        translates.add(globalNotificationTabButton);
        translates.add(worldNotificationTabButton);
        translates.trimToSize();
    }

    @Override
    public void act(final float delta) {
        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isUserNotificationChanged() ||
                Chati.CHATI.isNewNotificationReceived() || Chati.CHATI.isWorldChanged()) {
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

    @Override
    public void focus() {
        super.focus();
        if (getStage() != null) {
            getStage().setScrollFocus(notificationListScrollPane);
        }
    }

    /**
     * Zeigt die Liste aller globalen Benachrichtigungen an.
     */
    private void showGlobalNotifications() {
        globalNotificationEntries.clear();
        IUserManagerView userManager = Chati.CHATI.getUserManager();
        if (userManager.isLoggedIn() && userManager.getInternUserView() != null) {
            userManager.getInternUserView().getGlobalNotifications().values()
                    .forEach(notification -> globalNotificationEntries.add(new NotificationListEntry(notification)));
            layoutEntries(globalNotificationEntries);
        }
    }

    /**
     * Zeigt die Liste aller Benachrichtigungen in der aktuellen Welt an.
     */
    private void showWorldNotifications() {
        worldNotificationEntries.clear();
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
        if (internUser != null && internUser.isInCurrentWorld()) {
            internUser.getWorldNotifications().values()
                    .forEach(notification -> worldNotificationEntries.add(new NotificationListEntry(notification)));
            layoutEntries(worldNotificationEntries);
        }
    }

    /**
     * Deaktiviert den Tab zur Anzeige aller globalen Benachrichtigungen.
     */
    private void disableGlobalNotificationTab() {
        disableButton(globalNotificationTabButton);
        globalNotificationEntries.clear();
        notificationListContainer.clearChildren();
    }

    /**
     * Deaktiviert den Tab zur Anzeige aller Benachrichtigungen der aktuellen Welt.
     */
    private void disableWorldNotificationTab() {
        if (worldNotificationTabButton.isChecked() && !globalNotificationTabButton.isDisabled()) {
            worldNotificationTabButton.setChecked(false);
            globalNotificationTabButton.setChecked(true);
            showGlobalNotifications();
        }
        disableButton(worldNotificationTabButton);
        worldNotificationEntries.clear();
    }

    /**
     * Zeigt die übergebenen Einträge in der Liste an.
     * @param entries Anzuzeigende Einträge.
     */
    private void layoutEntries(@NotNull final Set<NotificationListEntry> entries) {
        notificationListContainer.clearChildren();
        entries.forEach(entry -> notificationListContainer.add(entry).growX().row());
    }
}
