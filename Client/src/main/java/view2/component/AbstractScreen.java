package view2.component;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;

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

    public MenuStage getStage() {
        return stage;
    }

    public abstract InputProcessor getInputProcessor();
}
