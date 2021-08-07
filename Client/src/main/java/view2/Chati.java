package view2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import controller.network.ServerSender;
import model.user.UserManager;
import view2.component.menu.MenuScreen;

public class Chati extends Game {

    public static final float PPM = 10; //pixels per meter

    private static Chati CHATI;

    private final ServerSender serverSender;
    private final UserManager userManager;
    private Screen currentScreen;

    public Chati(ServerSender serverSender, UserManager userManager) {
        CHATI = this;
        this.serverSender = serverSender;
        this.userManager = userManager;

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setFullscreenMode(Gdx.graphics.getDisplayMode());
        config.setIdleFPS(60);
        config.useVsync(true);
        config.setTitle("Chati");
        new Lwjgl3Application(this, config);
    }

    @Override
    public void create() {
        setScreen(new MenuScreen());
    }

    @Override
    public void setScreen(Screen screen) {
        currentScreen = screen;
        super.setScreen(screen);
    }

    @Override
    public Screen getScreen() {
        return currentScreen;
    }

    public ServerSender getServerSender() {
        return serverSender;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public static Chati getInstance() {
        return CHATI;
    }
}
