package view2.component.hud.notificationList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import controller.network.ServerSender;
import model.notification.INotificationView;
import model.notification.NotificationType;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.Assets;
import view2.component.ChatiToolTip;
import view2.component.AbstractWindow;

import java.time.format.DateTimeFormatter;

public class NotificationListEntry extends Table implements Comparable<NotificationListEntry> {

    private static final float SHOW_BUTTON_WIDTH = 150;
    private static final float BUTTON_SIZE = 30;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 7.5f;
    private static final float BUTTON_SCALE_FACTOR = 0.1f;

    private NotificationWindow openWindow;
    private Label titleLabel;
    private Label dateLabel;
    private TextButton showButton;
    private ImageButton acceptButton;
    private ImageButton declineButton;
    private ImageButton deleteButton;

    private final INotificationView notification;

    protected NotificationListEntry(INotificationView notification) {
        this.notification = notification;
        create();
        setLayout();
    }

    private void create() {
        titleLabel = new Label(notification.getType().getName(), Assets.SKIN);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        dateLabel = new Label(notification.getTimestamp().format(formatter), Assets.SKIN);

        showButton = new TextButton("Anzeigen", Assets.SKIN);
        showButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                openWindow = new NotificationWindow();
                openWindow.open();
            }
        });

        if (notification.getType() != NotificationType.INFORMATION) {
            acceptButton = new ImageButton(Assets.ACCEPT_ICON);
            acceptButton.addListener(new ChatiToolTip("Annehmen"));
        } else {
            acceptButton = new ImageButton(Assets.DISABLED_ACCEPT_ICON);
            acceptButton.setDisabled(true);
            acceptButton.setTouchable(Touchable.disabled);
        }
        acceptButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                acceptButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                acceptButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                new ConfirmWindow(NotificationAction.ACCEPT).open();
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    acceptButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    acceptButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        if (notification.getType() != NotificationType.INFORMATION) {
            declineButton = new ImageButton(Assets.DECLINE_ICON);
            declineButton.addListener(new ChatiToolTip("Ablehnen"));
        } else {
            declineButton = new ImageButton(Assets.DISABLED_DECLINE_ICON);
            declineButton.setDisabled(true);
            declineButton.setTouchable(Touchable.disabled);
        }
        declineButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                declineButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                declineButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                new ConfirmWindow(NotificationAction.DECLINE).open();
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    declineButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    declineButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });

        deleteButton = new ImageButton(Assets.DELETE_ICON);
        deleteButton.addListener(new ChatiToolTip("Löschen"));
        deleteButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                deleteButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                deleteButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                new ConfirmWindow(NotificationAction.DELETE).open();
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    deleteButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    deleteButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                }
            }
        });
    }

    private void setLayout() {
        NinePatchDrawable controlsBackground =
                new NinePatchDrawable(new NinePatch(Assets.SKIN.getRegion("panel1"), 10, 10, 10, 10));
        setBackground(controlsBackground);
        left().defaults().padLeft(HORIZONTAL_SPACING).padRight(HORIZONTAL_SPACING).padTop(VERTICAL_SPACING);

        Table labelContainer = new Table();
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

    private class NotificationWindow extends AbstractWindow {

        private static final float WINDOW_WIDTH = 750;
        private static final float WINDOW_HEIGHT = 350;
        private static final float ROW_HEIGHT = 60;
        private static final float SPACING = 15;

        private Label showLabel;
        private Label dateLabel;
        private TextButton okButton;
        private ImageButton acceptButton;
        private ImageButton declineButton;
        private ImageButton deleteButton;
        private TextButton closeButton;

        public NotificationWindow() {
            super(String.valueOf(titleLabel.getText()));
            create();
            setLayout();
        }

        @Override
        protected void create() {
            showLabel = new Label(notification.getMessageBundle().getMessageKey(), Assets.SKIN);
            showLabel.setWrap(true);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
            dateLabel = new Label(notification.getTimestamp().format(formatter), Assets.SKIN);

            okButton = new TextButton("Ok", Assets.SKIN);
            okButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    openWindow = null;
                    close();
                }
            });

            if (notification.getType() != NotificationType.INFORMATION) {
                acceptButton = new ImageButton(Assets.ACCEPT_ICON);
                acceptButton.addListener(new ChatiToolTip("Annehmen"));
            } else {
                acceptButton = new ImageButton(Assets.DISABLED_ACCEPT_ICON);
                acceptButton.setDisabled(true);
                acceptButton.setTouchable(Touchable.disabled);
            }
            acceptButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    acceptButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    acceptButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                    new ConfirmWindow(NotificationAction.ACCEPT).open();
                }
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer == -1) {
                        acceptButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                    }
                }
                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer == -1) {
                        acceptButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                    }
                }
            });

            if (notification.getType() != NotificationType.INFORMATION) {
                declineButton = new ImageButton(Assets.DECLINE_ICON);
                declineButton.addListener(new ChatiToolTip("Ablehnen"));
            } else {
                declineButton = new ImageButton(Assets.DISABLED_DECLINE_ICON);
                declineButton.setDisabled(true);
                declineButton.setTouchable(Touchable.disabled);
            }
            declineButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    declineButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    declineButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                    new ConfirmWindow(NotificationAction.DECLINE).open();
                }
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer == -1) {
                        declineButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                    }
                }
                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer == -1) {
                        declineButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                    }
                }
            });

            deleteButton = new ImageButton(Assets.DELETE_ICON);
            deleteButton.addListener(new ChatiToolTip("Löschen"));
            deleteButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    deleteButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    deleteButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                    new ConfirmWindow(NotificationAction.DELETE).open();
                }
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer == -1) {
                        deleteButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                    }
                }
                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer == -1) {
                        deleteButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                    }
                }
            });

            closeButton = new TextButton("X", Assets.SKIN);
            closeButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    openWindow = null;
                    close();
                }
            });
        }

        @Override
        protected void setLayout() {
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
            getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
        }
    }

    private class ConfirmWindow extends AbstractWindow {
        private static final float WINDOW_WIDTH = 550;
        private static final float WINDOW_HEIGHT = 275;
        private static final float ROW_HEIGHT = 60;
        private static final float SPACING = 15;

        private final NotificationAction action;
        private Label infoLabel;
        private TextButton confirmButton;
        private TextButton cancelButton;
        private TextButton closeButton;

        public ConfirmWindow(NotificationAction action) {
            super("Bestätigen");
            this.action = action;
            create();
            setLayout();
        }

        @Override
        protected void create() {
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
            infoLabel = new Label(text, Assets.SKIN);

            confirmButton = new TextButton("Ja", Assets.SKIN);
            confirmButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    switch (action) {
                        case ACCEPT:
                            Chati.CHATI.getServerSender()
                                    .send(ServerSender.SendAction.NOTIFICATION_RESPONSE, notification.getNotificationId(), true);
                            break;
                        case DECLINE:
                            Chati.CHATI.getServerSender()
                                    .send(ServerSender.SendAction.NOTIFICATION_RESPONSE, notification.getNotificationId(), false);
                            break;
                        case DELETE:
                            Chati.CHATI.getServerSender()
                                    .send(ServerSender.SendAction.NOTIFICATION_DELETE, notification.getNotificationId());
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + action);
                    }
                    if (openWindow != null) {
                        openWindow.close();
                        openWindow = null;
                    }
                    close();
                    NotificationListEntry.this.remove();
                }
            });

            cancelButton = new TextButton("Nein", Assets.SKIN);
            cancelButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    close();
                }
            });

            closeButton = new TextButton("X", Assets.SKIN);
            closeButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    close();
                }
            });
        }

        @Override
        protected void setLayout() {
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

            getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
        }
    }

    private enum NotificationAction {
        ACCEPT, DECLINE, DELETE
    }
}
