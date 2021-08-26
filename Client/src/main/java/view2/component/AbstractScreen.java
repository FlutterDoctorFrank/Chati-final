package view2.component;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import view2.Chati;
import view2.component.hud.HeadUpDisplay;

public abstract class AbstractScreen extends ScreenAdapter {

    protected final MenuStage stage;

    protected boolean active;

    protected AbstractScreen() {
        this.stage = new MenuStage();
    }

    @Override
    public void render(float delta) {
        if (!active) {
            return;
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
        stage.addActor(HeadUpDisplay.getInstance());
        stage.addActor(HeadUpDisplay.getInstance().getChatWindow());

        active = true;
    }

    @Override
    public void hide() {
        stage.clear();

        active = false;
    }

    @Override
    public void pause() {
        active = false;
    }

    @Override
    public void resume() {
        active = true;
    }

    public MenuStage getStage() {
        return stage;
    }

    public abstract InputProcessor getInputProcessor();
}
