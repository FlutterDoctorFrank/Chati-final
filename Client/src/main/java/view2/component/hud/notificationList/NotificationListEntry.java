package view2.component.hud.notificationList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import model.notification.INotificationView;
import view2.Chati;

import java.time.format.DateTimeFormatter;

public class NotificationListEntry extends Table {

    private static final Drawable ACCEPT_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/accept.png")));
    private static final Drawable DECLINE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/decline.png")));
    private static final Drawable DELETE_ICON = new TextureRegionDrawable(new TextureRegion(new Texture("icons/delete.jpg")));

    private static final float SHOW_BUTTON_WIDTH = 100;
    private static final float BUTTON_SIZE = 30;
    private static final float VERTICAL_SPACING = 5;
    private static final float HORIZONTAL_SPACING = 7.5f;
    private static final float LABEL_FONT_SCALE_FACTOR = 1.5f;

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
        String text;
        if (notification.isRequest()) {
            text = "Anfrage";
        } else {
            text = "Information";
        }
        titleLabel = new Label(text, Chati.SKIN);
        titleLabel.setFontScale(LABEL_FONT_SCALE_FACTOR);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        dateLabel = new Label(notification.getTimestamp().format(formatter), Chati.SKIN);
        dateLabel.setFontScale(LABEL_FONT_SCALE_FACTOR);

        showButton = new TextButton("Anzeigen", Chati.SKIN);
        showButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (notification.isRequest()) {
                    // ....
                } else {
                    getStage().addActor(new ShowNotificationDialog(notification.getMessageBundle().getMessageKey()));
                }
            }
        });

        acceptButton = new ImageButton(ACCEPT_ICON);
        acceptButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });

        declineButton = new ImageButton(DECLINE_ICON);
        declineButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });

        deleteButton = new ImageButton(DELETE_ICON);
        deleteButton.addListener(new InputListener() {
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
        if (notification.isRequest()) {
            buttonContainer.add(acceptButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padRight(HORIZONTAL_SPACING)
                    .space(HORIZONTAL_SPACING).padBottom(VERTICAL_SPACING);
            buttonContainer.add(declineButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padRight(HORIZONTAL_SPACING)
                    .space(HORIZONTAL_SPACING).padBottom(VERTICAL_SPACING);
        }
        buttonContainer.add(deleteButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padRight(HORIZONTAL_SPACING).padBottom(VERTICAL_SPACING);
        add(buttonContainer).fillX().expandX();
    }

    private class ShowNotificationDialog extends Dialog {

        private static final float WINDOW_WIDTH = 550;
        private static final float WINDOW_HEIGHT = 350;

        private final String showText;

        public ShowNotificationDialog(String showText) {
            super("Information", Chati.SKIN);
            this.showText = showText;
            create();
            setLayout();
        }

        private void create() {
            Label showLabel = new Label(showText, Chati.SKIN);
            showLabel.setFontScale(LABEL_FONT_SCALE_FACTOR);
            text(showLabel);

            TextButton okButton = new TextButton("Ok", Chati.SKIN);
            okButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    remove();
                }
            });
            button(okButton);

            TextButton deleteButton = new TextButton("Löschen", Chati.SKIN);
            deleteButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    remove();
                }
            });
            button(deleteButton);
        }

        private void setLayout() {
            setMovable(false);
            setPosition((Gdx.graphics.getWidth() - WINDOW_WIDTH) / 2f, (Gdx.graphics.getHeight() - WINDOW_HEIGHT) / 2f);
            setWidth(WINDOW_WIDTH);
            setHeight(WINDOW_HEIGHT);
        }
    }
}
