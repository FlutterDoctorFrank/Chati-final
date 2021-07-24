package controller.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import org.jetbrains.annotations.NotNull;

/**
 * Verwaltet die Verbindung zum Server und hält die Referenzen auf das Model und die View.
 */
public class ClientNetworkManager extends NetworkManager<Client> {

    private ServerConnection connection;

    public ClientNetworkManager() {
        super(new Client());
    }

    @Override
    public void connected(@NotNull final Connection connection) {
        if (connection.equals(this.endPoint)) {
            this.connection = new ServerConnection(this);
        }
    }

    @Override
    public void disconnected(@NotNull final Connection connection) {
        if (connection.equals(this.endPoint)) {
            this.endPoint.removeListener(this.connection);
            this.connection = null;
        }
    }
}
