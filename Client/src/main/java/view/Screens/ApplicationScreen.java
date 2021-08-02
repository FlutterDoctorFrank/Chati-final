package view.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import view.Chati;
import view.UIComponents.Hud;

public abstract class ApplicationScreen implements Screen {
    protected Chati game;
    protected SpriteBatch spriteBatch = new SpriteBatch();
    protected OrthographicCamera gamecam;
    protected Viewport gamePort;
    protected Hud hud;

    public ApplicationScreen(Chati game) {
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 102/255f, 102/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        hud.act();
        hud.draw();

    }

    public Chati getGame() {
        return game;
    }

    public Hud getHud() {
        return hud;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
