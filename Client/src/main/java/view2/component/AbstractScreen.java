package view2.component;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import view2.component.hud.HeadUpDisplay;

public abstract class AbstractScreen extends ScreenAdapter {

    protected final MenuStage stage;

    protected Response pendingResponse;

    protected boolean active;

    protected AbstractScreen() {
        this.stage = new MenuStage();
        this.pendingResponse = Response.NONE;
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        if (active) {
            stage.draw();
        }
    }

    @Override
    public void show() {
        stage.addActor(HeadUpDisplay.getInstance());
        stage.addActor(HeadUpDisplay.getInstance().getChatWindow());
        if (HeadUpDisplay.getInstance().isMenuOpen()) {
            stage.addActor(HeadUpDisplay.getInstance().getCurrentMenuWindow());
        }

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

    public void setPendingResponse(Response pendingResponse) {
        this.pendingResponse = pendingResponse;
    }
}
