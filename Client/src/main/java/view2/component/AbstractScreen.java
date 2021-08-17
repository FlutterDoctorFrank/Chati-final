package view2.component;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class AbstractScreen extends ScreenAdapter {

    protected final Stage stage;

    protected AbstractScreen() {
        this.stage = new Stage();
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
