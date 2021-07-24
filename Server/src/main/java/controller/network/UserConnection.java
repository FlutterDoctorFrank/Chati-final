package controller.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import controller.network.protocol.Packet;
import controller.network.protocol.PacketListener;
import controller.network.protocol.PacketListenerIn;
import org.jetbrains.annotations.NotNull;

/**
 * Empfängt und verarbeitet alle eingehenden Pakete eines einzelnen Clients und sendet Pakete an diesen Client.
 */
public class UserConnection extends Listener implements PacketListenerIn, ClientSender {

    private final ServerNetworkManager manager;
    private final Connection connection;

    public UserConnection(@NotNull final ServerNetworkManager manager, @NotNull final Connection connection) {
        this.manager = manager;
        this.connection = connection;

        connection.addListener(this);
    }

    @Override
    public void send(@NotNull final SendAction action, @NotNull final Object object) {
        if (this.connection.isConnected()) {
            this.connection.sendTCP(action.getPacket(object));
        }
    }

    @Override
    public void received(@NotNull final Connection connection, @NotNull final Object object) {
        // Sollte niemals der Fall sein
        if (!this.connection.equals(connection)) {
            connection.removeListener(this);
            return;
        }

        if (object instanceof Packet<?>) {
            try {
                call((Packet<?>) object, this);
            } catch (ClassCastException ex) {
                // Illegales Netzwerkpaket erhalten. Verbindung trennen.
                this.connection.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends PacketListener> void call(@NotNull final Packet<T> packet,
                                                        @NotNull final PacketListener listener) {
        packet.call((T) listener);
    }
}
