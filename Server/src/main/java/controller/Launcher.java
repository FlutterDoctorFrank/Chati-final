package controller;

import controller.network.ServerNetworkManager;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import model.context.global.GlobalContext;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Launcher implements Runnable {

    private final ServerNetworkManager network;

    private Launcher() {
        this.network = new ServerNetworkManager(UserAccountManager.getInstance(), GlobalContext.getInstance());
    }

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String input;

        try {
            while (this.network.isActive() && (input = reader.readLine()) != null) {
                if (input.isEmpty()) {
                    continue;
                }

                try {
                    final String[] arguments = input.split(" ");

                    Command.valueOf(arguments[0].toUpperCase()).execute(this, Arrays.copyOfRange(arguments, 1, arguments.length));
                } catch (IllegalArgumentException ex) {
                    System.out.println("Unknown command. Type 'help' for the commands.");
                }
            }
        } catch (IOException ex) {
            System.out.println("Exception in console input handling");
        }
    }

    /**
     * Startet die Server-Anwendung.
     */
    public void launch() {
        this.network.start();

        new Thread(this, "Console-Handler").start();
    }

    public static void main(@NotNull final String[] args) {
        final OptionParser parser = new OptionParser() {
            {
                this.acceptsAll(List.of("?", "help"), "Shows the help");

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

                System.out.println("Starting Server...");
                launcher.launch();
            }
        } catch (OptionException ex) {
            System.err.println("Failed to parse arguments: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Exception while launching/running application: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private enum Command {

        HELP("Shows all available commands.") {
            @Override
            public void execute(@NotNull final Launcher launcher, @NotNull final String[] arguments) {
                if (arguments.length > 0) {
                    System.out.println("Too many arguments for the help command.");
                    return;
                }

                System.out.println("Available commands:");
                for (final Command command : Command.values()) {
                    System.out.println("- " + command.name().toLowerCase() + ": " + command.description);
                }
            }
        },

        STOP("Stops the server.") {
            @Override
            public void execute(@NotNull final Launcher launcher, @NotNull final String[] arguments) {
                if (arguments.length > 0) {
                    System.out.println("Too many arguments for the stop command.");
                    return;
                }

                System.out.println("Stopping Server...");
                launcher.network.stop();
            }
        };

        private final String description;

        Command(@NotNull final String description) {
            this.description = description;
        }

        public abstract void execute(@NotNull final Launcher launcher, @NotNull final String[] arguments);
    }
}
