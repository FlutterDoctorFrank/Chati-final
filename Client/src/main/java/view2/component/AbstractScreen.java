package view2.component;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class AbstractScreen extends ScreenAdapter {

    protected final Stage stage;

    protected AbstractScreen() {
        this.stage = new Stage();
    }

    public Stage getStage() {
        return stage;
    }
}
