package view2.component.hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import view2.component.ChatiTable;

public class HeadUpDisplay extends Stage {

    private static final String USER_ICON_PATH = "icons/user_img.png";
    private static final String NOTIFICATION_ICON_PATH = "icons/speech_bubble_img.png";
    private static final String SETTINGS_ICON_PATH = "icons/settings_img.png";

    private ImageButton userListButton;
    private ImageButton notificationListButton;
    private ImageButton settingsButton;

    ButtonGroup<TextButton> hudButtons2;
    TextButton userListButton2;
    TextButton notificationListButton2;
    TextButton settingsButton2;

    public HeadUpDisplay() {
        create();
        setLayout();
    }

    protected void create() {
        ButtonGroup<ImageButton> hudButtons = new ButtonGroup<>();
        hudButtons.setMinCheckCount(0);
        hudButtons.setMaxCheckCount(1);
        hudButtons.setUncheckLast(true);

        TextureRegionDrawable userIcon = new TextureRegionDrawable(new TextureRegion(new Texture(USER_ICON_PATH)));
        TextureRegionDrawable userIconPressed = new TextureRegionDrawable(new TextureRegion(new Texture(USER_ICON_PATH)));
        userListButton = new ImageButton(userIcon, userIconPressed);
        userListButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }
        });

        TextureRegionDrawable notificationIcon = new TextureRegionDrawable(new TextureRegion(new Texture(NOTIFICATION_ICON_PATH)));
        TextureRegionDrawable notificationIconPressed = new TextureRegionDrawable(new TextureRegion(new Texture(NOTIFICATION_ICON_PATH)));
        notificationListButton = new ImageButton(notificationIcon, notificationIconPressed);
        notificationListButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });

        TextureRegionDrawable settingsIcon = new TextureRegionDrawable(new TextureRegion(new Texture(SETTINGS_ICON_PATH)));
        TextureRegionDrawable settingsIconPressed = new TextureRegionDrawable(new TextureRegion(new Texture(SETTINGS_ICON_PATH)));
        settingsButton = new ImageButton(settingsIcon, settingsIconPressed);
        settingsButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });

        hudButtons.add(userListButton);
        hudButtons.add(notificationListButton);
        hudButtons.add(settingsButton);
    }

    protected void setLayout() {
        int buttonSize = 60;
        int horizontalSpacing = 2;

        Table container = new Table();
        container.setFillParent(true);
        container.top().right();
        container.add(userListButton).width(buttonSize).height(buttonSize).space(horizontalSpacing);
        container.add(notificationListButton).width(buttonSize).height(buttonSize).space(horizontalSpacing);
        container.add(settingsButton).width(buttonSize).height(buttonSize);
        addActor(container);
    }
}
