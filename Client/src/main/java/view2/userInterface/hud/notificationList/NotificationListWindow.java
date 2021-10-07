package view2.userInterface.hud.notificationList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.MessageBundle;
import model.notification.INotificationView;
import model.notification.NotificationAction;
import model.user.IInternUserView;
import model.user.IUserManagerView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiImageButton;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTooltip;
import view2.userInterface.ChatiWindow;
import view2.userInterface.hud.HudMenuWindow;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.logging.Level;

/**
 * Eine Klasse, welche das Benachrichtigungsmenü des HeadUpDisplay repräsentiert.
 */
public class NotificationListWindow extends HudMenuWindow {

    private static final float BUTTON_SIZE = 30;

    private final Set<NotificationListEntry> currentEntries;
    private final ChatiTextButton globalNotificationTabButton;
    private final ChatiTextButton worldNotificationTabButton;
    private final ScrollPane notificationListScrollPane;
    private final Table notificationListContainer;
    private final ChatiImageButton readAllButton;
    private final ChatiImageButton deleteAllButton;
    private final ChatiLabel newNotificationsCountLabel;

    /**
     * Erzeugt eine neue Instanz des NotificationListWindow.
     */
    public NotificationListWindow() {
        super("window.title.notifications");
        this.currentEntries = new TreeSet<>();

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

        newNotificationsCountLabel = new ChatiLabel("window.notification.info", 0, 0);

        readAllButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_read"),
                Chati.CHATI.getDrawable("notification_action_read"),
                Chati.CHATI.getDrawable("notification_action_read_disabled"));
        readAllButton.addListener(new ChatiTooltip("hud.tooltip.read-all"));
        readAllButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ConfirmWindow(NotificationAction.READ).open();
            }
        });
        readAllButton.setDisabled(true);
        readAllButton.setTouchable(Touchable.disabled);

        deleteAllButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_delete"),
                Chati.CHATI.getDrawable("notification_action_delete"),
                Chati.CHATI.getDrawable("notification_action_delete_disabled"));
        deleteAllButton.addListener(new ChatiTooltip("hud.tooltip.delete-all"));
        deleteAllButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ConfirmWindow(NotificationAction.DELETE).open();
            }
        });
        deleteAllButton.setDisabled(true);
        deleteAllButton.setTouchable(Touchable.disabled);

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
        Table tabContainer = new Table();
        tabContainer.defaults().growX();
        tabContainer.add(globalNotificationTabButton, worldNotificationTabButton);
        add(tabContainer).growX().row();
        Table buttonContainer = new Table();
        buttonContainer.right();
        buttonContainer.defaults().size(BUTTON_SIZE).right().padLeft(SPACE / 2).padRight(SPACE / 2)
                .padBottom(SPACE / 4).padTop(SPACE / 4);
        newNotificationsCountLabel.setAlignment(Align.left, Align.left);
        buttonContainer.add(newNotificationsCountLabel).left().growX();
        buttonContainer.add(readAllButton, deleteAllButton);
        add(buttonContainer).growX().row();
        add(notificationListScrollPane).grow();

        // Translatable register
        translatables.add(globalNotificationTabButton);
        translatables.add(worldNotificationTabButton);
        translatables.add(newNotificationsCountLabel);
        translatables.trimToSize();
    }

    @Override
    public void act(final float delta) {
        if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isUserNotificationChanged() ||
                Chati.CHATI.isUserNotificationReceived() || Chati.CHATI.isWorldChanged()) {
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
        if (getStage() != null) {
            getStage().setScrollFocus(notificationListScrollPane);
        }
    }

    /**
     * Zeigt die Liste aller globalen Benachrichtigungen an.
     */
    private void showGlobalNotifications() {
        currentEntries.clear();
        IUserManagerView userManager = Chati.CHATI.getUserManager();
        if (userManager.isLoggedIn() && userManager.getInternUserView() != null) {
            userManager.getInternUserView().getGlobalNotifications().values()
                    .forEach(notification -> currentEntries.add(new NotificationListEntry(notification)));
            layoutEntries();
        }
    }

    /**
     * Zeigt die Liste aller Benachrichtigungen in der aktuellen Welt an.
     */
    private void showWorldNotifications() {
        currentEntries.clear();
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
        if (internUser != null && internUser.isInCurrentWorld()) {
            internUser.getWorldNotifications().values()
                    .forEach(notification -> currentEntries.add(new NotificationListEntry(notification)));
            layoutEntries();
        }
    }

    /**
     * Deaktiviert den Tab zur Anzeige aller globalen Benachrichtigungen.
     */
    private void disableGlobalNotificationTab() {
        disableButton(globalNotificationTabButton);
        currentEntries.clear();
        notificationListContainer.clearChildren();
    }

    /**
     * Deaktiviert den Tab zur Anzeige aller Benachrichtigungen der aktuellen Welt.
     */
    private void disableWorldNotificationTab() {
        disableButton(worldNotificationTabButton);
        currentEntries.clear();
        if (worldNotificationTabButton.isChecked() && !globalNotificationTabButton.isDisabled()) {
            worldNotificationTabButton.setChecked(false);
            globalNotificationTabButton.setChecked(true);
            showGlobalNotifications();
        }
    }

    /**
     * Zeigt die übergebenen Einträge in der Liste an.
     */
    private void layoutEntries() {
        notificationListContainer.clearChildren();
        currentEntries.forEach(entry -> notificationListContainer.add(entry).growX().row());

        int newNotificationsCount = (int) currentEntries.stream().map(NotificationListEntry::getNotification)
                .filter(Predicate.not(INotificationView::isRead)).count();
        if (currentEntries.size() == 0) {
            deleteAllButton.setDisabled(true);
            deleteAllButton.setTouchable(Touchable.disabled);
        } else {
            deleteAllButton.setDisabled(false);
            deleteAllButton.setTouchable(Touchable.enabled);
        }
        if (newNotificationsCount == 0) {
            readAllButton.setDisabled(true);
            readAllButton.setTouchable(Touchable.disabled);
        } else {
            readAllButton.setDisabled(false);
            readAllButton.setTouchable(Touchable.enabled);
        }
        newNotificationsCountLabel.setText(Chati.CHATI.getLocalization().format("window.notification.info",
                currentEntries.size(), newNotificationsCount));
    }

    @Override
    public void translate() {
        super.translate();
        updateNotificationCount();
    }

    /**
     * Aktualisiert die Anzeige der Benachrichtigungsanzahl.
     */
    private void updateNotificationCount() {
        int newNotificationsCount = (int) currentEntries.stream().map(NotificationListEntry::getNotification)
                .filter(Predicate.not(INotificationView::isRead)).count();
        newNotificationsCountLabel.setText(Chati.CHATI.getLocalization().format("window.notification.info",
                currentEntries.size(), newNotificationsCount));
    }

    /**
     * Eine Klasse, welche das Menü zum Bestätigen einer durchgeführten Benachrichtigungsaktion für alle
     * Benachrichtigungen repräsentiert.
     */
    private class ConfirmWindow extends ChatiWindow {

        private static final float WINDOW_WIDTH = 550;
        private static final float WINDOW_HEIGHT = 275;

        /**
         * Erzeugt eine neue Instanz des ConfirmWindow.
         * @param action Durchzuführende Benachrichtigungsaktion.
         */
        public ConfirmWindow(@NotNull final NotificationAction action) {
            super("window.title.confirm", WINDOW_WIDTH, WINDOW_HEIGHT);

            MessageBundle message;
            switch (action) {
                case READ:
                    message = new MessageBundle("window.notification.confirm-read-all");
                    break;
                case DELETE:
                    message = new MessageBundle("window.notification.confirm-delete-all");
                    break;
                default:
                    Chati.LOGGER.log(Level.WARNING, "Tried to open the confirm window for all" +
                            "notifications with an invalid action: " + action);
                    return;
            }

            ChatiLabel infoLabel = new ChatiLabel(message);
            ChatiTextButton confirmButton = new ChatiTextButton("menu.button.yes", true);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    switch (action) {
                        case READ:
                            currentEntries.stream().map(NotificationListEntry::getNotification)
                                    .forEach(notification -> Chati.CHATI.send(ServerSender.SendAction.NOTIFICATION_READ,
                                            notification.getNotificationId()));
                            break;
                        case DELETE:
                            currentEntries.stream().map(NotificationListEntry::getNotification)
                                    .forEach(notification -> Chati.CHATI.send(ServerSender.SendAction.NOTIFICATION_DELETE,
                                            notification.getNotificationId()));
                            break;
                        default:
                            Chati.LOGGER.log(Level.WARNING, "Tried to confirm an invalid action for" +
                                    "all notifications: " + action);
                            return;
                    }
                    close();
                }
            });

            ChatiTextButton cancelButton = new ChatiTextButton("menu.button.no", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    close();
                }
            });

            // Layout
            setModal(true);
            setMovable(false);

            Table container = new Table();
            container.defaults().height(ROW_HEIGHT).spaceBottom(SPACE).center().growX();
            infoLabel.setAlignment(Align.center, Align.center);
            infoLabel.setWrap(true);
            container.add(infoLabel).spaceBottom(2 * SPACE).row();
            Table buttonContainer = new Table();
            buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
            buttonContainer.add(confirmButton).padRight(SPACE / 2);
            buttonContainer.add(cancelButton).padLeft(SPACE / 2);
            container.add(buttonContainer);
            add(container).padLeft(SPACE).padRight(SPACE).grow();

            // Translatable register
            translatables.add(infoLabel);
            translatables.add(confirmButton);
            translatables.add(cancelButton);
            translatables.trimToSize();
        }

        @Override
        public void focus() {
        }
    }
}
