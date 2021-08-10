package view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import controller.Launcher;
import model.user.IUserManagerView;
import org.jetbrains.annotations.NotNull;
import view.Screens.ApplicationScreen;

public class Chati extends Game {

    private final Launcher launcher;
    private final IUserManagerView userManager;

    private ApplicationScreen currentScreen;
    public static final int V_WIDTH = 1280;
    public static final int V_HEIGHT = 680;
    public static final float PPM = 10; //pixels per meter

    public static final short GROUND_BIT = 1;
    public static final short AVATAR_BIT = 2;
    public static final short OBJECT_BIT = 4;
    public static final short INTERACTIVE_AREA_BIT = 8;

    public Chati(@NotNull final Launcher launcher, @NotNull final IUserManagerView manager) {
        this.launcher = launcher;
        this.userManager = manager;

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setIdleFPS(60);
        config.useVsync(true);
        config.setTitle("Chati");
        config.setMaximized(true);
        config.setResizable(true);
        new Lwjgl3Application(this, config);
    }

    //Test delete when not needed
    public Chati() {
        this.launcher = null;
        this.userManager = null;

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setIdleFPS(60);
        config.useVsync(true);
        config.setTitle("Chati");
        config.setMaximized(true);
        config.setResizable(true);
        new Lwjgl3Application(this, config);
    }

    @Override
    public void create() {
        this.setScreen(new ApplicationScreen(this));

        this.launcher.setModelObserver(this.currentScreen.getHud());
        this.launcher.setControllerView(this.currentScreen.getHud());
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

    public @NotNull IUserManagerView getUserManager() {
        return this.userManager;
    }
}
