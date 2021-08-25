package view2.component;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import view2.component.hud.HeadUpDisplay;

public abstract class AbstractScreen extends ScreenAdapter {

    protected final MenuStage stage;

    protected AbstractScreen() {
        this.stage = new MenuStage();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
        stage.addActor(HeadUpDisplay.getInstance());
    }

    @Override
    public void hide() {
        stage.clear();
    }

    public MenuStage getStage() {
        return stage;
    }

    public abstract InputProcessor getInputProcessor();
}
