package controller;

import controller.network.ServerNetworkManager;
import model.context.global.GlobalContext;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Launcher implements Runnable {

    private final ServerNetworkManager network;

    private Launcher() {
        this.network = new ServerNetworkManager(UserAccountManager.getInstance(), GlobalContext.getInstance());

        new Thread(this, "Console-Handler").start();
    }

    @Override
    public void run() {
        this.network.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String input;

        try {
            while (this.network.isActive() && (input = reader.readLine()) != null) {
                if (input.isEmpty()) {
                    continue;
                }

                try {
                    final String[] arguments = input.split(" ");

                    Command.valueOf(arguments[0].toUpperCase()).execute(this.network, Arrays.copyOfRange(arguments, 1, arguments.length));
                } catch (IllegalArgumentException ex) {
                    System.out.println("Unknown command. Type 'help' for the commands.");
                }
            }
        } catch (IOException ex) {
            System.out.println("Exception in console input handling");
        }
    }

    public static void main(@NotNull final String[] args) {
        new Launcher();
    }

    private enum Command {

        HELP("Shows all available commands.") {
            @Override
            public void execute(@NotNull final ServerNetworkManager network, @NotNull final String[] arguments) {
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
            public void execute(@NotNull final ServerNetworkManager network, @NotNull final String[] arguments) {
                if (arguments.length > 0) {
                    System.out.println("Too many arguments for the stop command.");
                    return;
                }

                System.out.println("Stopping Server...");
                network.stop();
            }
        };

        private final String description;

        Command(@NotNull final String description) {
            this.description = description;
        }

        public abstract void execute(@NotNull final ServerNetworkManager network, @NotNull final String[] arguments);
    }
}
