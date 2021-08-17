package view2.component.hud.notificationList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import controller.network.ServerSender;
import model.notification.INotificationView;
import model.notification.NotificationType;
import org.jetbrains.annotations.NotNull;
import view2.Chati;
import view2.Icon;
import view2.component.ChatiToolTip;
import view2.component.ChatiWindow;

import java.time.format.DateTimeFormatter;

public class NotificationListEntry extends Table implements Comparable<NotificationListEntry> {

    private static final float SHOW_BUTTON_WIDTH = 150;
    private static final float BUTTON_SIZE = 30;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 7.5f;
    private static final float LABEL_FONT_SCALE_FACTOR = 1.5f;
    private static final float BUTTON_SCALE_FACTOR = 0.1f;

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
        titleLabel = new Label(notification.getType().getName(), Chati.SKIN);
        titleLabel.setFontScale(LABEL_FONT_SCALE_FACTOR);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        dateLabel = new Label(notification.getTimestamp().format(formatter), Chati.SKIN);
        dateLabel.setFontScale(LABEL_FONT_SCALE_FACTOR);

        showButton = new TextButton("Anzeigen", Chati.SKIN);
        showButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                getStage().addActor(new NotificationWindow());
            }
        });

        if (notification.getType() != NotificationType.NOTIFICATION) {
            acceptButton = new ImageButton(Icon.ACCEPT_ICON);
            acceptButton.addListener(new ChatiToolTip("Annehmen"));
        } else {
            acceptButton = new ImageButton(Icon.DISABLED_ACCEPT_ICON);
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
                Chati.getInstance().getServerSender()
                        .send(ServerSender.SendAction.NOTIFICATION_RESPONSE, notification.getNotificationId(), true);
                remove();
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

        if (notification.getType() != NotificationType.NOTIFICATION) {
            declineButton = new ImageButton(Icon.DECLINE_ICON);
            declineButton.addListener(new ChatiToolTip("Ablehnen"));
        } else {
            declineButton = new ImageButton(Icon.DISABLED_DECLINE_ICON);
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
                Chati.getInstance().getServerSender()
                        .send(ServerSender.SendAction.NOTIFICATION_RESPONSE, notification.getNotificationId(), false);
                remove();
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

        deleteButton = new ImageButton(Icon.DELETE_ICON);
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
                Chati.getInstance().getServerSender()
                        .send(ServerSender.SendAction.NOTIFICATION_DELETE, notification.getNotificationId());
                remove();
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
                new NinePatchDrawable(new NinePatch(Chati.SKIN.getRegion("panel1"), 10, 10, 10, 10));
        setBackground(controlsBackground);

        left().defaults().padLeft(HORIZONTAL_SPACING).padRight(HORIZONTAL_SPACING).padTop(VERTICAL_SPACING);

        Table labelContainer = new Table();
        labelContainer.add(titleLabel).left().fillX().expandX();
        labelContainer.add(dateLabel).right().padRight(VERTICAL_SPACING);

        add(labelContainer).spaceBottom(VERTICAL_SPACING).height(BUTTON_SIZE).fillX().expandX().row();

        Table buttonContainer = new Table();
        buttonContainer.add(showButton).left().width(SHOW_BUTTON_WIDTH).height(BUTTON_SIZE).fillX().expandX()
                .padBottom(VERTICAL_SPACING).space(HORIZONTAL_SPACING);
        buttonContainer.add(acceptButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padRight(HORIZONTAL_SPACING)
                .space(HORIZONTAL_SPACING).padBottom(VERTICAL_SPACING);
        buttonContainer.add(declineButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padRight(HORIZONTAL_SPACING)
                .space(HORIZONTAL_SPACING).padBottom(VERTICAL_SPACING);

        buttonContainer.add(deleteButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padBottom(VERTICAL_SPACING);
        add(buttonContainer).fillX().expandX();
    }

    @Override
    public int compareTo(@NotNull NotificationListEntry other) {
        if (this.notification.getTimestamp().compareTo(other.notification.getTimestamp()) == 0) {
            return this.notification.getNotificationId().compareTo(other.notification.getNotificationId());
        } else {
            return this.notification.getTimestamp().compareTo(other.notification.getTimestamp());
        }
    }

    private class NotificationWindow extends ChatiWindow {

        private static final float WINDOW_WIDTH = 550;
        private static final float WINDOW_HEIGHT = 350;
        private static final float ROW_WIDTH = 450;
        private static final float ROW_HEIGHT = 60;

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
            showLabel = new Label(notification.getMessageBundle().getMessageKey(), Chati.SKIN);
            showLabel.setFontScale(LABEL_FONT_SCALE_FACTOR);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
            dateLabel = new Label(notification.getTimestamp().format(formatter), Chati.SKIN);
            dateLabel.setFontScale(LABEL_FONT_SCALE_FACTOR);

            okButton = new TextButton("Ok", Chati.SKIN);
            okButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    NotificationWindow.this.remove();
                }
            });

            if (notification.getType() != NotificationType.NOTIFICATION) {
                acceptButton = new ImageButton(Icon.ACCEPT_ICON);
                acceptButton.addListener(new ChatiToolTip("Annehmen"));
            } else {
                acceptButton = new ImageButton(Icon.DISABLED_ACCEPT_ICON);
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
                    Chati.getInstance().getServerSender()
                            .send(ServerSender.SendAction.NOTIFICATION_RESPONSE, notification.getNotificationId(), true);
                    NotificationListEntry.this.remove();
                    NotificationWindow.this.remove();
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

            if (notification.getType() != NotificationType.NOTIFICATION) {
                declineButton = new ImageButton(Icon.DECLINE_ICON);
                declineButton.addListener(new ChatiToolTip("Ablehnen"));
            } else {
                declineButton = new ImageButton(Icon.DISABLED_DECLINE_ICON);
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
                    Chati.getInstance().getServerSender()
                            .send(ServerSender.SendAction.NOTIFICATION_RESPONSE, notification.getNotificationId(), false);
                    NotificationListEntry.this.remove();
                    NotificationWindow.this.remove();
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

            deleteButton = new ImageButton(Icon.DELETE_ICON);
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
                    Chati.getInstance().getServerSender()
                            .send(ServerSender.SendAction.NOTIFICATION_DELETE, notification.getNotificationId());
                    NotificationListEntry.this.remove();
                    remove();
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

            closeButton = new TextButton("X", Chati.SKIN);
            closeButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    remove();
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
            defaults().padLeft(HORIZONTAL_SPACING).padRight(HORIZONTAL_SPACING);

            getTitleTable().add(dateLabel).right().padRight(VERTICAL_SPACING);

            left().defaults().padLeft(HORIZONTAL_SPACING).padRight(HORIZONTAL_SPACING).padTop(VERTICAL_SPACING);

            Table labelContainer = new Table(Chati.SKIN);
            labelContainer.add(showLabel).center();
            add(labelContainer).top().width(ROW_WIDTH).height(ROW_HEIGHT).fillY().expandY()
                    .spaceTop(HORIZONTAL_SPACING).spaceBottom(VERTICAL_SPACING).row();

            Table buttonContainer = new Table();
            buttonContainer.add(okButton).left().width(SHOW_BUTTON_WIDTH).height(BUTTON_SIZE).fillX().expandX()
                    .padBottom(VERTICAL_SPACING).padLeft(HORIZONTAL_SPACING).space(HORIZONTAL_SPACING);
            buttonContainer.add(acceptButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padRight(HORIZONTAL_SPACING)
                    .space(HORIZONTAL_SPACING).padBottom(VERTICAL_SPACING);
            buttonContainer.add(declineButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padRight(HORIZONTAL_SPACING)
                    .space(HORIZONTAL_SPACING).padBottom(VERTICAL_SPACING);
            buttonContainer.add(deleteButton).width(BUTTON_SIZE).height(BUTTON_SIZE)
                    .padBottom(VERTICAL_SPACING).padRight(HORIZONTAL_SPACING);
            add(buttonContainer).center().fillX().expandX().padBottom(VERTICAL_SPACING).row();

            getTitleTable().add(closeButton).right().width(getPadTop() * (2f/3f)).height(getPadTop() * (2f/3f));
        }
    }
}
