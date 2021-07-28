package view.UIComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import view.Chati;
import view.Screens.ScreenHandler;

public class Hud {
    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;

    public Hud(SpriteBatch spriteBatch, ScreenHandler screenHandler) {
        camera = new OrthographicCamera();
        viewport = new FitViewport(Chati.V_WIDTH, Chati.V_HEIGHT, camera);
        stage = new Stage(viewport, spriteBatch);
        Gdx.input.setInputProcessor(stage);
    }

    public Stage getStage() {
        return stage;
    }

    public void addTable(Table table) {
        stage.clear();
        stage.addActor(table);
    }


}
