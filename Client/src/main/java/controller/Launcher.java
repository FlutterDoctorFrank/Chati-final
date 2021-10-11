package controller;

import controller.network.ClientNetworkManager;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import model.user.UserManager;
import org.jetbrains.annotations.NotNull;
import view.Chati;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Launcher {

    private final ClientNetworkManager network;
    private final UserManager manager;

    private Launcher() {
        this.manager = UserManager.getInstance();
        this.network = new ClientNetworkManager(this.manager);
    }

    /**
     * Startet die Client-Anwendung.
     */
    public void launch() {
        final Chati view = new Chati(this.manager);

        this.manager.setModelObserver(view);
        this.network.setView(view);
        this.network.start();

        view.start();
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

                try {
                    final URL properties = Launcher.class.getClassLoader().getResource("logging.properties");
                    final File home = new File(System.getProperty("user.home"), "Chati");

                    if (properties != null && !properties.toString().startsWith("jar:")) {
                        System.setProperty("RunTexturePacker", "");
                    }

                    if (home.mkdirs()) {
                        System.out.println("Created home directory for Chati");
                    }

                    LogManager.getLogManager().readConfiguration(properties != null ? properties.openStream() : null);
                } catch (IOException | NullPointerException ex) {
                    System.err.println("Failed to load logging properties. Using default configuration.");
                }

                Logger.getLogger("chati").info("Starting Client...");
                launcher.launch();
            }
        } catch (OptionException ex) {
            System.err.println("Failed to parse arguments: " + ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger("chati").log(Level.SEVERE, "Exception while launching/running application", ex);
        }
    }
}
