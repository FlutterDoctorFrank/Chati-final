package view2;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import view2.userInterface.ChatiStage;
import view2.userInterface.Response;
import view2.userInterface.hud.HeadUpDisplay;

public abstract class ChatiScreen extends ScreenAdapter {

    protected final ChatiStage stage;

    protected Response pendingResponse;

    protected ChatiScreen() {
        this.stage = new ChatiStage();
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
    }

    @Override
    public void hide() {
        HeadUpDisplay.getInstance().hideChatWindow();
        HeadUpDisplay.getInstance().closeCurrentMenu();
        HeadUpDisplay.getInstance().closeCommunicationWindow();
        stage.clear();
    }

    public ChatiStage getStage() {
        return stage;
    }

    public void setPendingResponse(Response pendingResponse) {
        this.pendingResponse = pendingResponse;
    }

    public abstract InputProcessor getInputProcessor();
}
