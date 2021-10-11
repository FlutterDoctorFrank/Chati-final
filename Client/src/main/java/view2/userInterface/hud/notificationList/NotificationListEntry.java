package view2.userInterface.hud.notificationList;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.MessageBundle;
import model.notification.INotificationView;
import model.notification.NotificationAction;
import model.notification.NotificationType;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.ChatiLocalization.Translatable;
import view2.userInterface.ChatiImageButton;
import view2.userInterface.ChatiLabel;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiTooltip;
import view2.userInterface.ChatiWindow;
import java.sql.Timestamp;
import java.util.logging.Level;

/**
 * Eine Klasse, welche einen Eintrag in der Benachrichtigungsliste des HeadUpDisplay repräsentiert.
 */
public class NotificationListEntry extends Table implements Translatable, Comparable<NotificationListEntry> {

    private static final float SHOW_BUTTON_WIDTH = 150;
    private static final float BUTTON_SIZE = 30;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 7.5f;

    private final INotificationView notification;
    private final ChatiTextButton showButton;
    private final ChatiLabel typeLabel;
    private final ChatiLabel dateLabel;
    private NotificationTextWindow notificationTextWindow;

    /**
     * Erzeugt eine neue Instanz des NotificationListEntry.
     * @param notification Zum Eintrag zugehörige Benachrichtigung.
     */
    protected NotificationListEntry(@NotNull final INotificationView notification) {
        this.notification = notification;
        this.typeLabel = new ChatiLabel(notification.getType().getMessageKey());
        this.dateLabel = new ChatiLabel("pattern.notification.time", Timestamp.valueOf(notification.getTimestamp()));

        Image stateImage = new Image();
        if (!notification.isRead()) {
            stateImage.setDrawable(Chati.CHATI.getDrawable("notification_new"));
            stateImage.addListener(new ChatiTooltip("hud.tooltip.new"));
        } else if (notification.getType() == NotificationType.INFORMATION) {
            stateImage.setDrawable(Chati.CHATI.getDrawable("notification_read"));
            stateImage.addListener(new ChatiTooltip("hud.tooltip.read"));
        } else if (notification.isAccepted()) {
            stateImage.setDrawable(Chati.CHATI.getDrawable("notification_accepted"));
            stateImage.addListener(new ChatiTooltip("hud.tooltip.accepted"));
        } else if (notification.isDeclined()) {
            stateImage.setDrawable(Chati.CHATI.getDrawable("notification_declined"));
            stateImage.addListener(new ChatiTooltip("hud.tooltip.declined"));
        } else {
            stateImage.setDrawable(Chati.CHATI.getDrawable("notification_pending"));
            stateImage.addListener(new ChatiTooltip("hud.tooltip.pending"));
        }

        showButton = new ChatiTextButton("menu.button.show", true);
        showButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                if (!notification.isRead()) {
                    Chati.CHATI.send(ServerSender.SendAction.NOTIFICATION_READ, notification.getNotificationId());
                }
                notificationTextWindow = new NotificationTextWindow(notification);
                notificationTextWindow.open();
            }
        });

        ChatiImageButton acceptButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_accept"),
                Chati.CHATI.getDrawable("notification_action_accept"),
                Chati.CHATI.getDrawable("notification_action_accept_disabled"));
        acceptButton.addListener(new ChatiTooltip("hud.tooltip.accept"));
        acceptButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ConfirmWindow(NotificationAction.ACCEPT).open();
            }
        });
        if (notification.getType() == NotificationType.INFORMATION
                || notification.isAccepted() || notification.isDeclined()) {
            acceptButton.setDisabled(true);
            acceptButton.setTouchable(Touchable.disabled);
        }

        ChatiImageButton declineButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_decline"),
                Chati.CHATI.getDrawable("notification_action_decline"),
                Chati.CHATI.getDrawable("notification_action_decline_disabled"));
        declineButton.addListener(new ChatiTooltip("hud.tooltip.decline"));
        declineButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ConfirmWindow(NotificationAction.DECLINE).open();
            }
        });
        if (notification.getType() == NotificationType.INFORMATION
                || notification.isAccepted() || notification.isDeclined()) {
            declineButton.setDisabled(true);
            declineButton.setTouchable(Touchable.disabled);
        }

        ChatiImageButton deleteButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_delete"));
        deleteButton.addListener(new ChatiTooltip("hud.tooltip.delete"));
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ConfirmWindow(NotificationAction.DELETE).open();
            }
        });

        // Layout
        NinePatchDrawable background =
                new NinePatchDrawable(new NinePatch(Chati.CHATI.getSkin().getRegion("panel1"), 10, 10, 10, 10));
        setBackground(background);
        left().defaults().padLeft(HORIZONTAL_SPACING).padRight(HORIZONTAL_SPACING).padTop(VERTICAL_SPACING);

        Table labelContainer = new Table();
        labelContainer.add(stateImage).left().size(BUTTON_SIZE).padRight(VERTICAL_SPACING);
        labelContainer.add(typeLabel).left().growX();
        labelContainer.add(dateLabel).right();

        add(labelContainer).spaceBottom(VERTICAL_SPACING).height(BUTTON_SIZE).growX().row();

        Table buttonContainer = new Table();
        buttonContainer.defaults().size(BUTTON_SIZE).padLeft(HORIZONTAL_SPACING).padRight(HORIZONTAL_SPACING)
                .padBottom(VERTICAL_SPACING);
        buttonContainer.add(showButton).left().width(SHOW_BUTTON_WIDTH).growX();
        buttonContainer.add(acceptButton, declineButton, deleteButton);
        add(buttonContainer).growX();
    }

    /**
     * Gibt die Benachrichtigung dieses Eintrags zurück.
     * @return Benachrichtigungs dieses Eintrags.
     */
    public @NotNull INotificationView getNotification() {
        return notification;
    }

    @Override
    public int compareTo(@NotNull final NotificationListEntry other) {
        int readComp = Boolean.compare(this.notification.isRead(), other.notification.isRead());
        int handledComp = -Boolean.compare(other.notification.isAccepted() || other.notification.isDeclined(),
                this.notification.isAccepted() || this.notification.isDeclined());
        int acceptedComp = Boolean.compare(other.notification.isAccepted(), this.notification.isAccepted());
        int timeComp = other.notification.getTimestamp().compareTo(this.notification.getTimestamp());
        int idComp = this.notification.getNotificationId().compareTo(other.notification.getNotificationId());

        return readComp != 0 ? readComp
                : (handledComp != 0 ? handledComp
                : (acceptedComp != 0 ? acceptedComp
                : (timeComp != 0 ? timeComp
                : idComp)));
    }

    @Override
    public void translate() {
        typeLabel.translate();
        dateLabel.translate();
        showButton.translate();
    }

    /**
     * Eine Klasse, welche das Menü zur Anzeige des Benachrichtigungstexts einer Benachrichtigung repräsentiert.
     */
    private class NotificationTextWindow extends ChatiWindow {

        private static final float WINDOW_WIDTH = 750;
        private static final float WINDOW_HEIGHT = 400;

        private final INotificationView notification;
        private final ChatiImageButton acceptButton;
        private final ChatiImageButton declineButton;

        /**
         * Erzeugt eine neue Instanz des NotificationTextWindow.
         */
        public NotificationTextWindow(@NotNull final INotificationView notification) {
            super(notification.getType().getMessageKey(), WINDOW_WIDTH, WINDOW_HEIGHT);
            this.notification = notification;

            ChatiLabel dateLabel = new ChatiLabel("pattern.notification.time", Timestamp.valueOf(notification.getTimestamp()));
            ChatiLabel textLabel = new ChatiLabel(notification.getMessageBundle());
            textLabel.setWrap(true);

            ChatiTextButton okButton = new ChatiTextButton("menu.button.okay", true);
            okButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    notificationTextWindow = null;
                    close();
                }
            });

            acceptButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_accept"),
                    Chati.CHATI.getDrawable("notification_action_accept"),
                    Chati.CHATI.getDrawable("notification_action_accept_disabled"));
            acceptButton.addListener(new ChatiTooltip("hud.tooltip.accept"));
            acceptButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    new ConfirmWindow(NotificationAction.ACCEPT).open();
                }
            });
            if (notification.getType() == NotificationType.INFORMATION
                    || notification.isAccepted() || notification.isDeclined()) {
                acceptButton.setDisabled(true);
                acceptButton.setTouchable(Touchable.disabled);
            }

            declineButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_decline"),
                    Chati.CHATI.getDrawable("notification_action_decline"),
                    Chati.CHATI.getDrawable("notification_action_decline_disabled"));
            declineButton.addListener(new ChatiTooltip("hud.tooltip.decline"));
            declineButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    new ConfirmWindow(NotificationAction.DECLINE).open();
                }
            });
            if (notification.getType() == NotificationType.INFORMATION
                    || notification.isAccepted() || notification.isDeclined()) {
                declineButton.setDisabled(true);
                declineButton.setTouchable(Touchable.disabled);
            }


            ChatiImageButton deleteButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_delete"));
            deleteButton.addListener(new ChatiTooltip("hud.tooltip.delete"));
            deleteButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    new ConfirmWindow(NotificationAction.DELETE).open();
                }
            });

            // Layout
            setModal(true);
            setMovable(false);
            left().defaults().padLeft(HORIZONTAL_SPACING).padRight(HORIZONTAL_SPACING).padTop(VERTICAL_SPACING);

            Table container = new Table();
            container.defaults().height(ROW_HEIGHT).spaceBottom(SPACE).center().growX();
            dateLabel.setAlignment(Align.right, Align.right);
            container.add(dateLabel).spaceBottom(SPACE / 2).row();
            textLabel.setAlignment(Align.left, Align.left);
            container.add(textLabel).row();
            Table buttonContainer = new Table();
            buttonContainer.defaults().bottom().right().size(BUTTON_SIZE).space(SPACE);
            buttonContainer.add(okButton).left().height(BUTTON_SIZE).width(SHOW_BUTTON_WIDTH).growX();
            buttonContainer.add(acceptButton, declineButton, deleteButton);
            container.add(buttonContainer).bottom().padBottom(VERTICAL_SPACING).grow().row();
            add(container).padLeft(SPACE).padRight(SPACE).grow();

            // Translatable register
            translatables.add(dateLabel);
            translatables.add(textLabel);
            translatables.add(okButton);
            translatables.trimToSize();
        }

        @Override
        public void act(final float delta) {
            if (Chati.CHATI.isUserNotificationChanged()) {
                if (notification.isAccepted() || notification.isDeclined()) {
                    acceptButton.setDisabled(true);
                    acceptButton.setTouchable(Touchable.disabled);
                    declineButton.setDisabled(true);
                    declineButton.setTouchable(Touchable.disabled);
                }
            }
        }

        @Override
        public void focus() {
        }
    }

    /**
     * Eine Klasse, welche das Menü zum Bestätigen einer durchgeführten Benachrichtigungsaktion repräsentiert.
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
                case ACCEPT:
                    message = new MessageBundle("window.notification.confirm-accept", typeLabel.getText());
                    break;
                case DECLINE:
                    message = new MessageBundle("window.notification.confirm-decline", typeLabel.getText());
                    break;
                case DELETE:
                    message = new MessageBundle("window.notification.confirm-delete", typeLabel.getText());
                    break;
                default:
                    Chati.LOGGER.log(Level.WARNING, "Tried to open the confirm window for a single" +
                            "notification with an invalid action: " + action);
                    return;
            }

            ChatiLabel infoLabel = new ChatiLabel(message);
            ChatiTextButton confirmButton = new ChatiTextButton("menu.button.yes", true);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    switch (action) {
                        case ACCEPT:
                            Chati.CHATI.send(ServerSender.SendAction.NOTIFICATION_RESPONSE,
                                    notification.getNotificationId(), true);
                            break;
                        case DECLINE:
                            Chati.CHATI.send(ServerSender.SendAction.NOTIFICATION_RESPONSE,
                                    notification.getNotificationId(), false);
                            break;
                        case DELETE:
                            if (notificationTextWindow != null) {
                                notificationTextWindow.close();
                                notificationTextWindow = null;
                            }
                            Chati.CHATI.send(ServerSender.SendAction.NOTIFICATION_DELETE, notification.getNotificationId());
                            break;
                        default:
                            Chati.LOGGER.log(Level.WARNING, "Tried to confirm an invalid action " +
                                    "for a single notification: " + action);
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
