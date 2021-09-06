package view2.userInterface.hud.notificationList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.notification.INotificationView;
import model.notification.NotificationAction;
import model.notification.NotificationType;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.userInterface.ChatiImageButton;
import view2.userInterface.ChatiTextButton;
import view2.userInterface.ChatiToolTip;
import view2.userInterface.ChatiWindow;

import java.time.format.DateTimeFormatter;

public class NotificationListEntry extends Table implements Comparable<NotificationListEntry> {

    private static final float SHOW_BUTTON_WIDTH = 150;
    private static final float BUTTON_SIZE = 30;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 7.5f;

    private NotificationWindow notificationWindow;
    private final Label titleLabel;

    private final INotificationView notification;

    protected NotificationListEntry(INotificationView notification) {
        this.notification = notification;

        titleLabel = new Label(notification.getType().getName(), Chati.CHATI.getSkin());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        Label dateLabel = new Label(notification.getTimestamp().format(formatter), Chati.CHATI.getSkin());

        Image stateImage = new Image();
        if (!notification.isRead()) {
            stateImage.setDrawable(Chati.CHATI.getDrawable("notification_new"));
            stateImage.addListener(new ChatiToolTip("Neu"));
        } else if (notification.isAccepted()) {
            stateImage.setDrawable(Chati.CHATI.getDrawable("notification_accepted"));
            stateImage.addListener(new ChatiToolTip("Angenommen"));
        } else if (notification.isDeclined()) {
            stateImage.setDrawable(Chati.CHATI.getDrawable("notification_declined"));
            stateImage.addListener(new ChatiToolTip("Abgelehnt"));
        } else {
            stateImage.setDrawable(Chati.CHATI.getDrawable("notification_pending"));
            stateImage.addListener(new ChatiToolTip("Ausstehend"));
        }

        ChatiTextButton showButton = new ChatiTextButton("Anzeigen", true);
        showButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!notification.isRead()) {
                    Chati.CHATI.send(ServerSender.SendAction.NOTIFICATION_READ, notification.getNotificationId());
                }
                notificationWindow = new NotificationWindow();
                notificationWindow.open();
            }
        });

        ChatiImageButton acceptButton;
        if (notification.getType() != NotificationType.INFORMATION
                && !notification.isAccepted() && !notification.isDeclined()) {
            acceptButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_accept"));
            acceptButton.addListener(new ChatiToolTip("Annehmen"));
        } else {
            acceptButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_accept_disabled"));
            acceptButton.setDisabled(true);
            acceptButton.setTouchable(Touchable.disabled);
        }
        acceptButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new ConfirmWindow(NotificationAction.ACCEPT).open();
            }
        });

        ChatiImageButton declineButton;
        if (notification.getType() != NotificationType.INFORMATION
                && !notification.isAccepted() && !notification.isDeclined()) {
            declineButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_decline"));
            declineButton.addListener(new ChatiToolTip("Ablehnen"));
        } else {
            declineButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_decline_disabled"));
            declineButton.setDisabled(true);
            declineButton.setTouchable(Touchable.disabled);
        }
        declineButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new ConfirmWindow(NotificationAction.DECLINE).open();
            }
        });

        ChatiImageButton deleteButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_delete"));
        deleteButton.addListener(new ChatiToolTip("Löschen"));
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
        labelContainer.add(titleLabel).left().growX();
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
    public int compareTo(@NotNull NotificationListEntry other) {
        if (other.notification.getTimestamp().compareTo(this.notification.getTimestamp()) == 0) {
            return this.notification.getNotificationId().compareTo(other.notification.getNotificationId());
        } else {
            return other.notification.getTimestamp().compareTo(this.notification.getTimestamp());
        }
    }

    private class NotificationWindow extends ChatiWindow {

        private static final float WINDOW_WIDTH = 750;
        private static final float WINDOW_HEIGHT = 350;

        public NotificationWindow() {
            super(String.valueOf(titleLabel.getText()));

            Label showLabel = new Label(notification.getMessageBundle().getMessageKey(), Chati.CHATI.getSkin());
            showLabel.setWrap(true);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
            Label dateLabel = new Label(notification.getTimestamp().format(formatter), Chati.CHATI.getSkin());

            TextButton okButton = new ChatiTextButton("Ok", true);
            okButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    notificationWindow = null;
                    close();
                }
            });

            ImageButton acceptButton;
            if (notification.getType() != NotificationType.INFORMATION
                    && !notification.isAccepted() && !notification.isDeclined()) {
                acceptButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_accept"));
                acceptButton.addListener(new ChatiToolTip("Annehmen"));
            } else {
                acceptButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_accept_disabled"));
                acceptButton.setDisabled(true);
                acceptButton.setTouchable(Touchable.disabled);
            }
            acceptButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    new ConfirmWindow(NotificationAction.ACCEPT).open();
                }
            });

            ImageButton declineButton;
            if (notification.getType() != NotificationType.INFORMATION
                    && !notification.isAccepted() && !notification.isDeclined()) {
                declineButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_decline"));
                declineButton.addListener(new ChatiToolTip("Ablehnen"));
            } else {
                declineButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_decline_disabled"));
                declineButton.setDisabled(true);
                declineButton.setTouchable(Touchable.disabled);
            }
            declineButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    new ConfirmWindow(NotificationAction.DECLINE).open();
                }
            });

            ImageButton deleteButton = new ChatiImageButton(Chati.CHATI.getDrawable("notification_action_delete"));
            deleteButton.addListener(new ChatiToolTip("Löschen"));
            deleteButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
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
            showLabel.setAlignment(Align.center, Align.center);
            container.add(showLabel).row();
            Table buttonContainer = new Table();
            buttonContainer.defaults().bottom().right().size(BUTTON_SIZE).space(SPACING);
            buttonContainer.add(okButton).left().height(ROW_HEIGHT).width(SHOW_BUTTON_WIDTH).growX();
            buttonContainer.add(acceptButton, declineButton, deleteButton);
            container.add(buttonContainer).bottom().padBottom(VERTICAL_SPACING).grow().row();
            add(container).padLeft(SPACING).padRight(SPACING).grow();

            getTitleTable().add(dateLabel).right().padRight(VERTICAL_SPACING);
        }
    }

    private class ConfirmWindow extends ChatiWindow {
        private static final float WINDOW_WIDTH = 550;
        private static final float WINDOW_HEIGHT = 275;

        public ConfirmWindow(NotificationAction action) {
            super("Bestätigen");

            String text;
            switch (action) {
                case ACCEPT:
                    text = "Bist du sicher, dass du diese " + titleLabel.getText() + " annehmen möchtest?";
                    break;
                case DECLINE:
                    text = "Bist du sicher, dass du diese " + titleLabel.getText() + " ablehnen möchtest?";
                    break;
                case DELETE:
                    text = "Bist du sicher, dass du diese " + titleLabel.getText() + " löschen möchtest?";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + action);
            }

            Label infoLabel = new Label(text, Chati.CHATI.getSkin());

            ChatiTextButton confirmButton = new ChatiTextButton("Ja", true);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
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
                            if (notificationWindow != null) {
                                notificationWindow.close();
                                notificationWindow = null;
                            }
                            Chati.CHATI.send(ServerSender.SendAction.NOTIFICATION_DELETE, notification.getNotificationId());
                            break;
                        default:
                            throw new IllegalArgumentException("Unexpected notification action.");
                    }
                    close();
                }
            });

            ChatiTextButton cancelButton = new ChatiTextButton("Nein", true);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
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
        }
    }
}
