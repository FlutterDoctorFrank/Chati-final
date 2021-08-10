package view2.component.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class HeadUpDisplay extends Stage {

    private static final String USER_ICON_CHECKED_PATH = "icons/userIcon.png";
    private static final String USER_ICON_PATH = "icons/userIconChecked.png";
    private static final String NOTIFICATION_ICON_CHECKED_PATH = "icons/notificationIcon.png";
    private static final String NOTIFICATION_ICON_PATH = "icons/notificationIconChecked.png";
    private static final String SETTINGS_ICON_CHECKED_PATH = "icons/settingsIcon.png";
    private static final String SETTINGS_ICON_PATH = "icons/settingsIconChecked.png";
    private static final float BUTTON_SCALE_FACTOR = 0.1f;

    private Table currentListContainer;

    private ImageButton userListButton;
    private ImageButton notificationListButton;
    private ImageButton settingsButton;

    public HeadUpDisplay() {
        create();
        setLayout();
    }

    protected void create() {
        currentListContainer = new Table();
        currentListContainer.setFillParent(true);

        TextureRegionDrawable userIcon = new TextureRegionDrawable(new TextureRegion(new Texture(USER_ICON_PATH)));
        TextureRegionDrawable userIconChecked = new TextureRegionDrawable(new TextureRegion(new Texture(USER_ICON_CHECKED_PATH)));
        TextureRegionDrawable notificationIcon = new TextureRegionDrawable(new TextureRegion(new Texture(NOTIFICATION_ICON_PATH)));
        TextureRegionDrawable notificationIconChecked = new TextureRegionDrawable(new TextureRegion(new Texture(NOTIFICATION_ICON_CHECKED_PATH)));
        TextureRegionDrawable settingsIcon = new TextureRegionDrawable(new TextureRegion(new Texture(SETTINGS_ICON_PATH)));
        TextureRegionDrawable settingsIconChecked = new TextureRegionDrawable(new TextureRegion(new Texture(SETTINGS_ICON_CHECKED_PATH)));

        ButtonGroup<ImageButton> hudButtons = new ButtonGroup<>();
        hudButtons.setMinCheckCount(0);
        hudButtons.setMaxCheckCount(1);
        hudButtons.setUncheckLast(true);

        userListButton = new ImageButton(userIcon);
        userListButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                userListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                userListButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (userListButton.isChecked()) {
                    userListButton.getStyle().imageUp = userIconChecked;
                    notificationListButton.getStyle().imageUp = notificationIcon;
                    settingsButton.getStyle().imageUp = settingsIcon;
                    currentListContainer.clearChildren();
                    currentListContainer.addActor(new UserListTable());
                } else {
                    userListButton.getStyle().imageUp = userIcon;
                    currentListContainer.clearChildren();
                }
            }
        });

        notificationListButton = new ImageButton(notificationIcon, notificationIconChecked);
        notificationListButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                notificationListButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                notificationListButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (notificationListButton.isChecked()) {
                    notificationListButton.getStyle().imageUp = notificationIconChecked;
                    userListButton.getStyle().imageUp = userIcon;
                    settingsButton.getStyle().imageUp = settingsIcon;
                    currentListContainer.clearChildren();
                } else {
                    notificationListButton.getStyle().imageUp = notificationIcon;
                    currentListContainer.clearChildren();
                }
            }
        });

        settingsButton = new ImageButton(settingsIcon, settingsIconChecked);
        settingsButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                settingsButton.getImage().scaleBy(-BUTTON_SCALE_FACTOR);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                settingsButton.getImage().scaleBy(BUTTON_SCALE_FACTOR);
                if (settingsButton.isChecked()) {
                    settingsButton.getStyle().imageUp = settingsIconChecked;
                    userListButton.getStyle().imageUp = userIcon;
                    notificationListButton.getStyle().imageUp = notificationIcon;
                    currentListContainer.clearChildren();
                } else {
                    settingsButton.getStyle().imageUp = settingsIcon;
                    currentListContainer.clearChildren();
                }

                if (!settingsButton.isChecked()) {
                    settingsButton.getStyle().imageUp = settingsIcon;
                    currentListContainer.clearChildren();
                } else {
                    settingsButton.getStyle().imageUp = settingsIconChecked;
                    if (hudButtons.getChecked() != null) {
                        if (hudButtons.getChecked().equals(userListButton)) {
                            userListButton.getStyle().imageUp = userIcon;
                        } else {
                            notificationListButton.getStyle().imageUp = notificationIcon;
                        }
                    }
                    currentListContainer.clearChildren();
                    // Schließe vorherige Menüs, öffne Settingsmenü.
                }
            }
        });

        hudButtons.add(userListButton);
        hudButtons.add(notificationListButton);
        hudButtons.add(settingsButton);
    }

    protected void setLayout() {
        int buttonSize = 75;
        int horizontalSpacing = 2;

        Table container = new Table();
        container.setFillParent(true);
        container.top().right();
        container.add(userListButton).width(buttonSize).height(buttonSize).space(horizontalSpacing);
        container.add(notificationListButton).width(buttonSize).height(buttonSize).space(horizontalSpacing);
        container.add(settingsButton).width(buttonSize).height(buttonSize);
        addActor(container);

        addActor(currentListContainer);
    }
}
