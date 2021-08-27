package view2.component;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl3.audio.Mp3;
import view2.Chati;
import view2.component.hud.HeadUpDisplay;

public abstract class AbstractScreen extends ScreenAdapter {

    protected final MenuStage stage;

    protected Response pendingResponse;

    protected boolean active;

    protected Music music;

    protected AbstractScreen() {
        this.stage = new MenuStage();
        this.pendingResponse = Response.NONE;

        //Musik spielen. Die folgenden 3 Zeilen kann man durch die playMusic methode ersetzen.
        //Momentan ist die musik null, wenn man die anwendung startet, lade ich direkt die musikdatei.
        music = Gdx.audio.newMusic(Gdx.files.internal("music/music_1.mp3"));
        music.setLooping(true);
        music.play();

        //playMusic();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        if (active) {
            stage.draw();
        }

        if (Chati.CHATI.isMusicChanged()) {
            playMusic();
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

    private void playMusic() {
        music = Gdx.audio.newMusic(Gdx.files.internal(Chati.CHATI.getUserManager().getInternUserView().getMusic().getPath()));
        music.setLooping(true);
        music.play();
    }
}
