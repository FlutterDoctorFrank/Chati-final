package view2.component.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import view2.Chati;

public class Hud extends Stage {

    ButtonGroup<ImageButton> hudButtons;
    ImageButton userListButton;
    ImageButton notificationListButton;
    ImageButton settingsButton;

    public Hud() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()),
                Chati.getInstance().getSpriteBatch());
        create();

    }

    private void create() {
        ButtonGroup<ImageButton> hudButtons = new ButtonGroup<>();

        userListButton = new ImageButton(null, null, null);
        userListButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }
        });

        notificationListButton = new ImageButton(null, null, null);
        notificationListButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });

        settingsButton = new ImageButton(null, null, null);
        settingsButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        });
    }
}
