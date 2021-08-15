package view2.component.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import view2.component.hud.HeadUpDisplay;

public class WorldScreen extends ScreenAdapter {

    private static WorldScreen worldScreen;

    private final Stage stage;

    private WorldScreen() {
        this.stage = new Stage();
        stage.addActor(HeadUpDisplay.getInstance());
        createWorld();
    }

    private void createWorld() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public static WorldScreen getInstance() {
        if (worldScreen == null) {
            worldScreen = new WorldScreen();
        }
        return worldScreen;
    }
}
