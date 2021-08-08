package view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import controller.network.ServerSender;
import model.context.Context;
import model.user.UserManager;
import view.Screens.ApplicationScreen;

public class Chati extends Game {
    private ServerSender serverSender;
    private Context context;
    private UserManager userManager;
    private ApplicationScreen currentScreen;
    public static final int V_WIDTH = 1280;
    public static final int V_HEIGHT = 680;
    public static final float PPM = 10; //pixels per meter

    public static final short DEFAULT_BIT = 1;
    public static final short AVATAR_BIT = 2;
    public static final short OBJECT_BIT = 4;

    public Chati(ServerSender serverSender, Context context, UserManager userManager) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setIdleFPS(60);
        config.useVsync(true);
        config.setTitle("Chati");

        this.context = context;
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
        setScreen(new ApplicationScreen(this));
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

    public Context getContext() {
        return context;
    }

    public UserManager getUserManager() {
        return userManager;
    }
}
