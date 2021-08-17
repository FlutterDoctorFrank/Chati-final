package view2.component;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class AbstractScreen extends ScreenAdapter {

    protected final MenuStage stage;

    protected AbstractScreen() {
        this.stage = new MenuStage();
        stage.addListener(new InputListener() {
            public boolean keyDown(InputEvent event, int keycode) {
                if (Input.Keys.ESCAPE == keycode && stage.getKeyboardFocus() != null) {
                    stage.setKeyboardFocus(null);
                    return false;
                }
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public abstract InputProcessor getInputProcessor();
}
