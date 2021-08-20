package controller;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import controller.network.ClientNetworkManager;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import model.user.UserManager;
import org.jetbrains.annotations.NotNull;
import view.Chati;
import view.Screens.IModelObserver;
import view2.ViewControllerInterface;
import java.io.IOException;
import java.util.List;

public class Launcher {

    private final ClientNetworkManager network;
    private final UserManager manager;

    private Launcher() {
        this.manager = UserManager.getInstance();
        this.network = new ClientNetworkManager(this.manager);
    }

    /**
     * Setzt im Controller die Schnittstelle zur View.
     * @param observer die View-Schnittstelle für den Controller.
     */
    public void setControllerView(@NotNull final ViewControllerInterface observer) {
        this.network.setView(observer);
        this.network.start();
    }

    /**
     * Setzt im Model die Schnittstelle zur View.
     * @param observer die View-Schnittstelle für die View.
     */
    public void setModelObserver(@NotNull final IModelObserver observer) {
        this.manager.setModelObserver(observer);
    }

    /**
     * Startet die Client-Anwendung.
     */
    public void launch(final boolean second) {
        if (second) {
            view2.Chati chati = new view2.Chati(this.manager);
            setControllerView(chati);
            setModelObserver(chati);

            Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
            config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
            config.setIdleFPS(30);
            config.setTitle("Chati");
            new Lwjgl3Application(chati, config);
            return;
        }

        new Chati(this, this.manager);
    }

    public static void main(@NotNull final String[] args) {
        final OptionParser parser = new OptionParser() {
            {
                this.acceptsAll(List.of("?", "help"), "Shows the help");

                this.acceptsAll(List.of("h", "host", "server-ip"), "Host to listen on")
                        .withRequiredArg()
                        .ofType(String.class)
                        .describedAs("Hostname or IP");

                this.acceptsAll(List.of("t", "tcp-port", "server-tcp-port"), "TCP-Port to listen on")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .describedAs("TCP-Port");

                this.acceptsAll(List.of("u", "udp-port", "server-upd-port"), "UDP-Port to listen on")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .describedAs("UDP-Port");

                this.acceptsAll(List.of("v", "view-only"), "Runs the view without the other components");

                this.acceptsAll(List.of("s", "view-second"), "Runs the application with the second view");
            }
        };

        try {
            final OptionSet options = parser.parse(args);

            if (options == null || options.has("?")) {
                try {
                    parser.printHelpOn(System.out);
                } catch (IOException ex) {
                    System.err.println("Failed to print help: " + ex.getMessage());
                }
            } else if (options.has("view-only")) {
                try {
                    new Chati();
                } catch (Exception ex) {
                    System.err.println("Exception while running application view: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                final Launcher launcher = new Launcher();

                if (options.has("host")) {
                    final String host = (String) options.valueOf("host");

                    if (host.isEmpty()) {
                        System.err.println("Host/IP can not be empty.");
                        return;
                    }

                    launcher.network.setHost(host);
                }

                if (options.has("tcp-port")) {
                    final int tcpPort = (int) options.valueOf("tcp-port");

                    if (tcpPort < 0) {
                        System.err.println("TCP-Port must be greater than 0.");
                        return;
                    }

                    launcher.network.setPorts(tcpPort, -1);
                }

                if (options.has("udp-port")) {
                    final int udpPort = (int) options.valueOf("udp-port");

                    if (udpPort < 0) {
                        System.err.println("TCP-Port must be greater than 0.");
                        return;
                    }

                    launcher.network.setPorts(-1, udpPort);
                }

                System.out.println("Starting Client...");
                launcher.launch(options.has("view-second"));
            }
        } catch (OptionException ex) {
            System.err.println("Failed to parse arguments: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Exception while launching/running application: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
