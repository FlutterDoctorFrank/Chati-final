package controller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGL20;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import controller.network.ServerNetworkManager;
import controller.network.UserConnection;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import model.context.global.GlobalContext;
import model.exception.UserNotFoundException;
import model.role.Role;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Launcher implements Runnable {

    private final ServerNetworkManager network;
    private final UserAccountManager manager;
    private final GlobalContext global;

    private Launcher() {
        this.global = GlobalContext.getInstance();
        this.manager = UserAccountManager.getInstance();
        this.network = new ServerNetworkManager(this.manager, this.global);
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

                    Command.fromName(arguments[0].toUpperCase()).execute(this, Arrays.copyOfRange(arguments, 1, arguments.length));
                } catch (IllegalArgumentException ex) {
                    System.out.println("Unknown command. Type 'help' for the commands.");
                } catch (Exception ex) {
                    System.err.println("Exception while running command: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.err.println("Exception in console input handling.");
            ex.printStackTrace();
        }
    }

    /**
     * Startet die Server-Anwendung.
     */
    public void launch() {
        this.network.start();

        new HeadlessApplication(new ApplicationAdapter() {
            @Override
            public void create() {
                Gdx.gl = new MockGL20();
            }
        });

        this.global.load();
        this.manager.load();
        this.global.loadWorldInfos();

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

                try {
                    final InputStream properties = Launcher.class.getClassLoader().getResourceAsStream("logging.properties");
                    final File home = new File(System.getProperty("user.dir"), "logs");

                    if (home.mkdirs()) {
                        System.out.println("Created logs directory for Chati");
                    }

                    LogManager.getLogManager().readConfiguration(properties);
                } catch (IOException | NullPointerException ex) {
                    System.err.println("Failed to load logging properties. Using default configuration.");
                }

                Logger.getLogger("chati").info("Starting Server...");
                launcher.launch();
            }
        } catch (OptionException ex) {
            System.err.println("Failed to parse arguments: " + ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger("chati").log(Level.SEVERE, "Exception while launching/running application", ex);
        }
    }

    private enum Command {

        HELP("Shows all available commands.") {
            @Override
            public void execute(@NotNull final Launcher launcher, @NotNull final String[] arguments) {
                if (arguments.length > 0) {
                    System.out.println("Invalid usage: help");
                    return;
                }

                System.out.println("Available commands:");
                for (final Command command : Command.values()) {
                    System.out.println("- " + command.name().toLowerCase() + ": " + command.description);
                }
            }
        },

        LIST("List all connected connections/users.") {
            @Override
            public void execute(@NotNull final Launcher launcher, @NotNull final String[] arguments) {
                if (arguments.length > 0) {
                    System.out.println("Invalid usage: list");
                    return;
                }

                final Collection<String> users = launcher.network.getConnections().stream()
                        .map(UserConnection::toString).collect(Collectors.toList());

                if (users.isEmpty()) {
                    System.out.println("There are currently no connected connections/users.");
                    return;
                }

                System.out.println("There are currently " + users.size() + " connected connections/users:");
                System.out.println(String.join(", ", users));
            }
        },

        SET_OWNER("set-owner", "Sets the owner of this server.") {
            @Override
            public void execute(@NotNull final Launcher launcher, @NotNull final String[] arguments) {
                if (arguments.length != 1) {
                    System.out.println("Invalid usage: set-owner <name>");
                    return;
                }

                try {
                    final User target = launcher.manager.getUser(arguments[0]);

                    if (target.hasRole(launcher.global, Role.OWNER)) {
                        System.err.println("User " + target.getUsername() + " has already been set as Owner.");
                        return;
                    }

                    target.addRole(launcher.global, Role.OWNER);
                    System.out.println("User " + target.getUsername() + " has been set as Owner.");
                } catch (UserNotFoundException ex) {
                    System.err.println("User " + arguments[0] + " does not exist.");
                }
            }
        },

        UNSET_OWNER("unset-owner", "Unsets the owner for this server.") {
            @Override
            public void execute(@NotNull final Launcher launcher, @NotNull final String[] arguments) {
                if (arguments.length != 1) {
                    System.out.println("Invalid usage: unset-owner <name>");
                    return;
                }

                try {
                    final User target = launcher.manager.getUser(arguments[0]);

                    if (!target.hasRole(launcher.global, Role.OWNER)) {
                        System.err.println("User " + target.getUsername() + " has not been set as Owner.");
                        return;
                    }

                    target.removeRole(launcher.global, Role.OWNER);
                    System.out.println("User " + target.getUsername() + " has been unset as Owner.");
                } catch (UserNotFoundException ex) {
                    System.err.println("User " + arguments[0] + " does not exist.");
                }
            }
        },

        STOP("Stops the server.") {
            @Override
            public void execute(@NotNull final Launcher launcher, @NotNull final String[] arguments) {
                if (arguments.length > 0) {
                    System.out.println("Invalid usage: stop");
                    return;
                }

                System.out.println("Stopping Server...");
                launcher.network.stop();
                Gdx.app.exit();
            }
        };

        private final String name;
        private final String description;

        Command(@NotNull final String description) {
            this.name = this.name().toLowerCase();
            this.description = description;
        }

        Command(@NotNull final String name, @NotNull final String description) {
            this.name = name;
            this.description = description;
        }

        public abstract void execute(@NotNull final Launcher launcher, @NotNull final String[] arguments);

        public static @NotNull Command fromName(@NotNull final String name) throws IllegalArgumentException {
            try {
                return Command.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException ex) {
                for (final Command command : Command.values()) {
                    if (name.equalsIgnoreCase(command.name)) {
                        return command;
                    }
                }

                throw new IllegalArgumentException("There is no command with name " + name);
            }
        }
    }
}
