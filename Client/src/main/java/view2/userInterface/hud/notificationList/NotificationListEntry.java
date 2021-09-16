package view2.userInterface.hud.notificationList;

import com.badlogic.gdx.Gdx;
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

        ChatiImageButton acceptButton;
        if (notification.getType() != NotificationType.INFORMATION
                && !notification.isAccepted() && !notification.isDeclined()) {
            acceptButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_accept"));
            acceptButton.addListener(new ChatiTooltip("hud.tooltip.accept"));
        } else {
            acceptButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_accept_disabled"));
            acceptButton.setDisabled(true);
            acceptButton.setTouchable(Touchable.disabled);
        }
        acceptButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ConfirmWindow(NotificationAction.ACCEPT).open();
            }
        });

        ChatiImageButton declineButton;
        if (notification.getType() != NotificationType.INFORMATION
                && !notification.isAccepted() && !notification.isDeclined()) {
            declineButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_decline"));
            declineButton.addListener(new ChatiTooltip("hud.tooltip.decline"));
        } else {
            declineButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_decline_disabled"));
            declineButton.setDisabled(true);
            declineButton.setTouchable(Touchable.disabled);
        }
        declineButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ConfirmWindow(NotificationAction.DECLINE).open();
            }
        });

        ChatiImageButton deleteButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_delete"));
        deleteButton.addListener(new ChatiTooltip("hud.tooltip.delete"));
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                new ConfirmWindow(NotificationAction.DELETE).open();
            }
        });

        // Layout
        NinePatchDrawable controlsBackground =
                new NinePatchDrawable(new NinePatch(Chati.CHATI.getSkin().getRegion("panel1"), 10, 10, 10, 10));
        setBackground(controlsBackground);
        left().defaults().padLeft(HORIZONTAL_SPACING).padRight(HORIZONTAL_SPACING).padTop(VERTICAL_SPACING);

        Table labelContainer = new Table();
        labelContainer.add(stateImage).left().size(BUTTON_SIZE).padRight(VERTICAL_SPACING);
        labelContainer.add(typeLabel).left().growX();
        labelContainer.add(dateLabel).right().padRight(VERTICAL_SPACING);

        add(labelContainer).spaceBottom(VERTICAL_SPACING).height(BUTTON_SIZE).growX().row();

        Table buttonContainer = new Table();
        buttonContainer.defaults().size(BUTTON_SIZE).padRight(HORIZONTAL_SPACING).padBottom(VERTICAL_SPACING)
                .space(HORIZONTAL_SPACING);
        buttonContainer.add(showButton).left().width(SHOW_BUTTON_WIDTH).growX();
        buttonContainer.add(acceptButton, declineButton, deleteButton);
        add(buttonContainer).growX();
    }

    @Override
    public int compareTo(@NotNull final NotificationListEntry other) {
        if (other.notification.getTimestamp().compareTo(this.notification.getTimestamp()) == 0) {
            return this.notification.getNotificationId().compareTo(other.notification.getNotificationId());
        } else {
            return other.notification.getTimestamp().compareTo(this.notification.getTimestamp());
        }
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
            super(notification.getType().getMessageKey());
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

            if (notification.getType() != NotificationType.INFORMATION
                    && !notification.isAccepted() && !notification.isDeclined()) {
                acceptButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_accept"),
                        Chati.CHATI.getDrawable("notification_action_accept"),
                        Chati.CHATI.getDrawable("notification_action_accept_disabled"));
                acceptButton.addListener(new ChatiTooltip("hud.tooltip.accept"));
            } else {
                acceptButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_accept_disabled"));
                acceptButton.setDisabled(true);
                acceptButton.setTouchable(Touchable.disabled);
            }
            acceptButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    new ConfirmWindow(NotificationAction.ACCEPT).open();
                }
            });

            if (notification.getType() != NotificationType.INFORMATION
                    && !notification.isAccepted() && !notification.isDeclined()) {
                declineButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_decline"),
                        Chati.CHATI.getDrawable("notification_action_decline"),
                        Chati.CHATI.getDrawable("notification_action_decline_disabled"));
                declineButton.addListener(new ChatiTooltip("hud.tooltip.decline"));
            } else {
                declineButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_decline_disabled"));
                declineButton.setDisabled(true);
                declineButton.setTouchable(Touchable.disabled);
            }
            declineButton.addListener(new ClickListener() {
                @Override
                public void clicked(@NotNull final InputEvent event, final float x, final float y) {
                    new ConfirmWindow(NotificationAction.DECLINE).open();
                }
            });

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
            setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
            setWidth(WINDOW_WIDTH);
            setHeight(WINDOW_HEIGHT);
            left().defaults().padLeft(HORIZONTAL_SPACING).padRight(HORIZONTAL_SPACING).padTop(VERTICAL_SPACING);

            Table container = new Table();
            container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
            dateLabel.setAlignment(Align.right, Align.right);
            container.add(dateLabel).spaceBottom(SPACING / 2).row();
            textLabel.setAlignment(Align.left, Align.left);
            container.add(textLabel).row();
            Table buttonContainer = new Table();
            buttonContainer.defaults().bottom().right().size(BUTTON_SIZE).space(SPACING);
            buttonContainer.add(okButton).left().height(BUTTON_SIZE).width(SHOW_BUTTON_WIDTH).growX();
            buttonContainer.add(acceptButton, declineButton, deleteButton);
            container.add(buttonContainer).bottom().padBottom(VERTICAL_SPACING).grow().row();
            add(container).padLeft(SPACING).padRight(SPACING).grow();

            // Translatable register
            translates.add(dateLabel);
            translates.add(textLabel);
            translates.add(okButton);
            translates.trimToSize();
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
            super("window.title.confirm");

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
                    throw new IllegalStateException("Unexpected value: " + action);
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
                            throw new IllegalArgumentException("Unexpected notification action.");
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
            setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
            setWidth(WINDOW_WIDTH);
            setHeight(WINDOW_HEIGHT);

            Table container = new Table();
            container.defaults().height(ROW_HEIGHT).spaceBottom(SPACING).center().growX();
            infoLabel.setAlignment(Align.center, Align.center);
            infoLabel.setWrap(true);
            container.add(infoLabel).spaceBottom(2 * SPACING).row();
            Table buttonContainer = new Table();
            buttonContainer.defaults().colspan(2).height(ROW_HEIGHT).growX();
            buttonContainer.add(confirmButton).padRight(SPACING / 2);
            buttonContainer.add(cancelButton).padLeft(SPACING / 2);
            container.add(buttonContainer);
            add(container).padLeft(SPACING).padRight(SPACING).grow();

            // Translatable register
            translates.add(infoLabel);
            translates.add(confirmButton);
            translates.add(cancelButton);
            translates.trimToSize();
        }
    }
}
