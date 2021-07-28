package view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import view.Screens.*;
import view.UIComponents.LoginTable;

public class Chati extends Game {
    public static final int V_WIDTH = 1280;
    public static final int V_HEIGHT = 680;
    public static final float PPM = 10; //pixels per meter
    private ScreenHandler screenHandler = new ScreenHandler(this);

    public Chati() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setIdleFPS(60);
        config.useVsync(true);
        config.setTitle("Chati");

        config.setMaximized(true);
        config.setResizable(true);
        new Lwjgl3Application(this, config);

    }

    public void create() {
        setScreen(new MenuScreen(screenHandler));
    }

    @Override
    public void render() {
        super.render();
    }

}
