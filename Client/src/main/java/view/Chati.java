package view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import controller.network.ServerSender;
import model.context.global.GlobalContext;
import model.user.UserManager;
import view.Screens.ApplicationScreen;
import view.Screens.MenuScreen;

public class Chati extends Game {
    private ServerSender serverSender;
    private GlobalContext globalContext;
    private UserManager userManager;
    private ApplicationScreen currentScreen;
    public static final int V_WIDTH = 1280;
    public static final int V_HEIGHT = 680;
    public static final float PPM = 10; //pixels per meter

    public Chati(ServerSender serverSender, GlobalContext globalContext, UserManager userManager) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setIdleFPS(60);
        config.useVsync(true);
        config.setTitle("Chati");

        this.globalContext = globalContext;
        this.serverSender = serverSender;
        this.serverSender = serverSender;

        config.setMaximized(true);
        config.setResizable(false);
        new Lwjgl3Application(this, config);
    }

    public Chati () {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setIdleFPS(60);
        config.useVsync(true);
        config.setTitle("Chati");
        config.setMaximized(true);
        config.setResizable(true);
        new Lwjgl3Application(this, config);
    }

    public void create() {
        setScreen(new MenuScreen(this));
    }

    @Override
    public void setScreen(Screen screen) {
        currentScreen = (ApplicationScreen) screen;
        super.setScreen(screen);
    }

    @Override
    public ApplicationScreen getScreen() {
        return currentScreen;
    }

    @Override
    public void render() {
        super.render();
    }

    public ServerSender getServerSender() {
        return serverSender;
    }

    public GlobalContext getGlobalContext() {
        return globalContext;
    }

    public UserManager getUserManager() {
        return userManager;
    }
}
