package view2.component.hud;

import com.badlogic.gdx.Gdx;
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

    private ButtonGroup<ImageButton> hudButtons;
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

    private void create2() {
        hudButtons2 = new ButtonGroup<>();
        hudButtons2.setMinCheckCount(0);
        hudButtons2.setMaxCheckCount(1);
        hudButtons2.setUncheckLast(true);

        userListButton2 = new TextButton("U", ChatiTable.SKIN);
        userListButton2.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                userListButton2.setChecked(true);
                addActor(new UserListTable());
            }
        });


        notificationListButton2 = new TextButton("N", ChatiTable.SKIN);
        settingsButton2 = new TextButton("S", ChatiTable.SKIN);

        hudButtons2.add(userListButton2);
        hudButtons2.add(notificationListButton2);
        hudButtons2.add(settingsButton2);
    }

    private void setLayout2() {
        int buttonSize = 60;
        int horizontalSpacing = 0;

        Table headUpDisplayTable = new Table();
        headUpDisplayTable.setFillParent(true);
        headUpDisplayTable.align(Align.topRight);
        HorizontalGroup container = new HorizontalGroup();
        container.space(horizontalSpacing);
        container.setHeight(buttonSize);
        container.setWidth(3 * (buttonSize + horizontalSpacing));
        container.grow();

        container.addActor(userListButton2);
        container.addActor(notificationListButton2);
        container.addActor(settingsButton2);
        headUpDisplayTable.add(container).width(container.getWidth()).height(container.getHeight());
        addActor(headUpDisplayTable);
    }

    // Irgendwie krieg ichs nich hin, dass mir die ImageButtons angezeigt werden...
    protected void create() {
        hudButtons = new ButtonGroup<>();
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
        int horizontalSpacing = 0;

        Table headUpDisplayTable = new Table();
        headUpDisplayTable.setFillParent(true);
        headUpDisplayTable.align(Align.topRight);
        HorizontalGroup container = new HorizontalGroup();
        container.space(horizontalSpacing);
        container.setHeight(buttonSize);
        container.setWidth(3 * (buttonSize + horizontalSpacing));
        container.grow();

        /*
        userListButton.setWidth(buttonSize);
        userListButton.setHeight(buttonSize);
        notificationListButton.setWidth(buttonSize);
        notificationListButton.setHeight(buttonSize);
        settingsButton.setWidth(buttonSize);
        settingsButton.setHeight(buttonSize);

         */

        container.addActor(userListButton);
        container.addActor(notificationListButton);
        container.addActor(settingsButton);
        headUpDisplayTable.add(container).width(container.getWidth()).height(container.getHeight());
        addActor(headUpDisplayTable);

        /*
        HorizontalGroup buttonContainer = new HorizontalGroup();
        buttonContainer.setFillParent(true);
        buttonContainer.space(horizontalSpacing);
        buttonContainer.setWidth(3 * (buttonSize + horizontalSpacing));
        buttonContainer.setHeight(buttonSize);
        buttonContainer.addActor(userListButton);
        buttonContainer.addActor(notificationListButton);
        buttonContainer.addActor(settingsButton);
        buttonContainer.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Align.bottomRight);

        addActor(buttonContainer);


         */
    }
}
