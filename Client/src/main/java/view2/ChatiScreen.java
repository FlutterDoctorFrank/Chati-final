package view2;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import org.jetbrains.annotations.NotNull;
import view2.Localization.Translatable;
import view2.userInterface.hud.HeadUpDisplay;

/**
 * Eine abstrakte Klasse, die einen Bildschirm der Anwendung repräsentiert.
 */
public abstract class ChatiScreen extends ScreenAdapter implements Translatable {

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
    public void render(final float delta) {
        stage.act(delta);
        stage.draw();
        super.render(delta);
    }

    @Override
    public void show() {
        stage.addActor(Chati.CHATI.getHeadUpDisplay());
        stage.addActor(Chati.CHATI.getHeadUpDisplay().getChatWindow());
        super.show();
    }

    @Override
    public void hide() {
        Chati.CHATI.getHeadUpDisplay().hideChatWindow();
        Chati.CHATI.getHeadUpDisplay().closeCurrentMenu();
        Chati.CHATI.getHeadUpDisplay().closeCommunicationWindow();
        stage.clear();
        super.hide();
    }

    @Override
    public void dispose() {
        stage.dispose();
        super.dispose();
    }

    /**
     * Setzt die Information darüber, auf welche Antwort gerade gewartet wird.
     * @param pendingResponse Information, auf welche Antwort gerade gewartet wird.
     */
    public void setPendingResponse(@NotNull final Response pendingResponse) {
        this.pendingResponse = pendingResponse;
    }

    /**
     * Gibt die Stage dieses Bildschirms zurück.
     * @return Stage des Bildschirms.
     */
    public @NotNull ChatiStage getStage() {
        return stage;
    }

    /**
     * Gibt den InputProcessor dieses Bildschirms zurück.
     * @return InputProcessor des Bildschirms.
     */
    public abstract @NotNull InputProcessor getInputProcessor();
}
