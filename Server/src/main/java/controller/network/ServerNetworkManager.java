package controller.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import model.context.global.IGlobalContext;
import model.user.account.IUserAccountManager;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Reagiert auf eingehende Verbindungen und verwaltet die bestehenden Verbindungen.
 */
public class ServerNetworkManager extends NetworkManager<Server> {

    private static final int BUFFER_SIZE = (int) Math.pow(2, 24);

    private static final Logger LOGGER = Logger.getLogger("chati.network");
    private final Map<Integer, UserConnection> connections;

    private final IUserAccountManager accountManager;
    private final IGlobalContext global;

    public ServerNetworkManager(@NotNull final IUserAccountManager accountManager,
                                @NotNull final IGlobalContext global) {
        super(new Server(BUFFER_SIZE, BUFFER_SIZE));

        this.connections = new ConcurrentHashMap<>();
        this.accountManager = accountManager;
        this.global = global;
    }

    @Override
    public void connected(@NotNull final Connection connection) {
        if (this.connections.containsKey(connection.getID())) {
            LOGGER.warning("Previous connection with id " + connection.getID() + " has not been cleaned up. Cleaning it up...");
            final UserConnection previous = this.connections.remove(connection.getID());

            previous.disconnected(connection);
            connection.removeListener(previous);
        }

        LOGGER.info("Connection " + connection.getID() + " connected");
        final UserConnection handler = new UserConnection(this, connection);

        connection.addListener(handler);

        this.connections.put(connection.getID(), handler);
    }

    @Override
    public void disconnected(@NotNull final Connection connection) {
        LOGGER.info("Connection " + connection.getID() + " disconnected");
        if (this.connections.containsKey(connection.getID())) {
            final UserConnection handler = this.connections.remove(connection.getID());

            connection.removeListener(handler);
        }
    }

    /**
     * Gibt die mit dem Server verbundenen Verbindung zurück.
     * @return die Menge der verbundenen Benutzer.
     */
    public @NotNull Collection<UserConnection> getConnections() {
        return Collections.unmodifiableCollection(this.connections.values());
    }

    /**
     * Gibt die Instanz des Account Managers zurück.
     * @return die IUserAccountManager-Instanz.
     */
    public @NotNull IUserAccountManager getAccountManager() {
        return this.accountManager;
    }

    /**
     * Gibt die Instanz auf den globalen Kontext zurück.
     * @return die IGlobalContext-Instanz.
     */
    public @NotNull IGlobalContext getGlobal() {
        return this.global;
    }

    @Override
    public void start() {
        if (this.tcp == this.udp) {
            throw new IllegalStateException("TCP- and UDP-Port can not be equal");
        }

        try {
            this.endPoint.start();
            this.endPoint.bind(this.tcp);
            this.active = true;

            LOGGER.info(String.format("Hosted Server on port: %d", this.tcp));
        } catch (IOException ex) {
            this.active = false;

            LOGGER.warning(String.format("Failed to host Server on port: %d", this.tcp));
        }
    }

    @Override
    public void stop() {
        this.endPoint.stop();
        this.active = false;

        LOGGER.info("Closed Server.");
    }
}
