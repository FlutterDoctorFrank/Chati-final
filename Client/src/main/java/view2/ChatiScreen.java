package view2;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import view2.userInterface.hud.HeadUpDisplay;

/**
 * Eine abstrakte Klasse, die einen Bildschirm der Anwendung repräsentiert.
 */
public abstract class ChatiScreen extends ScreenAdapter {

    protected final ChatiStage stage;

    protected Response pendingResponse;

    /**
     * Erzeugt eine neue Instanz eines ChatiScreen.
     */
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

    /**
     * Setzt die Information darüber, auf welche Antwort gerade gewartet wird.
     * @param pendingResponse Information, auf welche Antwort gerade gewartet wird.
     */
    public void setPendingResponse(Response pendingResponse) {
        this.pendingResponse = pendingResponse;
    }

    /**
     * Gibt die Stage dieses Bildschirms zurück.
     * @return Stage des Bildschirms.
     */
    public ChatiStage getStage() {
        return stage;
    }

    /**
     * Gibt den InputProcessor dieses Bildschirms zurück.
     * @return InputProcessor des Bildschirms.
     */
    public abstract InputProcessor getInputProcessor();
}
