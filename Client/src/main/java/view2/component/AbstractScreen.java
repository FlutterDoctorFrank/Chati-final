package view2.component;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import view2.Chati;
import view2.component.hud.HeadUpDisplay;

public abstract class AbstractScreen extends ScreenAdapter {

    protected final MenuStage stage;

    protected Response pendingResponse;

    protected AbstractScreen() {
        this.stage = new MenuStage();
        this.pendingResponse = Response.NONE;
    }

    @Override
    public void render(float delta) {
        Chati.CHATI.getAudioManager().update();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
        stage.addActor(HeadUpDisplay.getInstance());
        stage.addActor(HeadUpDisplay.getInstance().getChatWindow());
        if (HeadUpDisplay.getInstance().isMenuOpen()) {
            stage.addActor(HeadUpDisplay.getInstance().getCurrentMenuWindow());
        }
    }

    @Override
    public void hide() {
        stage.clear();
    }

    public MenuStage getStage() {
        return stage;
    }

    public abstract InputProcessor getInputProcessor();

    public void setPendingResponse(Response pendingResponse) {
        this.pendingResponse = pendingResponse;
    }
}
