package controller.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import controller.network.protocol.Packet;
import controller.network.protocol.PacketListener;
import controller.network.protocol.PacketListenerOut;
import org.jetbrains.annotations.NotNull;

/**
 * Empfängt und verarbeitet alle eingehenden Pakete vom Server und sendet Pakete an den Server.
 */
public class ServerConnection extends Listener implements PacketListenerOut, ServerSender {

    private final ClientNetworkManager manager;

    public ServerConnection(@NotNull final ClientNetworkManager manager) {
        this.manager = manager;

        manager.getEndPoint().addListener(this);
    }

    @Override
    public void send(@NotNull final SendAction action, @NotNull final Object... objects) {
        if (this.manager.getEndPoint().isConnected()) {
            this.manager.getEndPoint().sendTCP(action.getPacket(objects));
        }
    }

    @Override
    public void received(@NotNull final Connection connection, @NotNull final Object object) {
        // Sollte niemals der Fall sein
        if (!this.manager.getEndPoint().equals(connection)) {
            connection.removeListener(this);
            return;
        }

        if (object instanceof Packet<?>) {
            try {
                call((Packet<?>) object, this);
            } catch (ClassCastException ex) {
                // Illegales Netzwerkpaket erhalten. Verbindung trennen.
                this.manager.getEndPoint().close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends PacketListener> void call(@NotNull final Packet<T> packet,
                                                        @NotNull final PacketListener listener) {
        packet.call((T) listener);
    }
}
