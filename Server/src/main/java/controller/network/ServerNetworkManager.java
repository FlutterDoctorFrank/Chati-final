package controller.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import model.context.global.IGlobalContext;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reagiert auf eingehende Verbindungen und verwaltet die bestehenden Verbindungen.
 */
public class ServerNetworkManager extends NetworkManager<Server> {

    private final Map<Integer, UserConnection> connections;
    private final IGlobalContext global;

    protected ServerNetworkManager(@NotNull final IGlobalContext global) {
        super(new Server());

        this.connections = new ConcurrentHashMap<>();
        this.global = global;
    }

    @Override
    public void connected(@NotNull final Connection connection) {
        this.connections.put(connection.getID(), new UserConnection(this, connection));
    }

    @Override
    public void disconnected(@NotNull final Connection connection) {
        if (this.connections.containsKey(connection.getID())) {
            final UserConnection handler = this.connections.remove(connection.getID());

            connection.removeListener(handler);
        }
    }

    public @NotNull IGlobalContext getGlobal() {
        return this.global;
    }
}
