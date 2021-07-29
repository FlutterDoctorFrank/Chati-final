package controller.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import controller.network.protocol.Packet;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketListener;
import controller.network.protocol.PacketListenerOut;
import controller.network.protocol.PacketOutContextInfo;
import controller.network.protocol.PacketOutContextJoin;
import controller.network.protocol.PacketOutContextMusic;
import controller.network.protocol.PacketOutContextRole;
import controller.network.protocol.PacketProfileAction;
import controller.network.protocol.PacketWorldAction;
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

    public void send(@NotNull final Packet<?> packet) {
        if (this.manager.getEndPoint().isConnected()) {
            this.manager.getEndPoint().sendTCP(packet);
        }
    }

    @Override
    public void send(@NotNull final SendAction action, @NotNull final Object... objects) {
        this.send(action.getPacket(objects));
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

    @Override
    public void handle(@NotNull final PacketAvatarMove packet) {
        // Die Server-Anwendung muss die Benutzer-ID setzen.
        if (packet.getUserId() != null) {
            //TODO Verarbeitung des AvatarMove-Pakets implementieren.
        }
    }

    @Override
    public void handle(@NotNull final PacketWorldAction packet) {
        //TODO Verarbeitung des WorldAction-Pakets implementieren.
    }

    @Override
    public void handle(@NotNull final PacketProfileAction packet) {
        //TODO Verarbeitung des ProfileAction-Pakets implementieren.
    }

    @Override
    public void handle(@NotNull final PacketOutContextInfo packet) {
        //TODO Verarbeitung des ContextInfo-Pakets implementieren.
    }

    @Override
    public void handle(@NotNull final PacketOutContextJoin packet) {
        //TODO Verarbeitung des ContextJoin-Pakets implementieren.
    }

    @Override
    public void handle(@NotNull final PacketOutContextMusic packet) {
        //TODO Verarbeitung des ContextMusic-Pakets implementieren.
    }

    @Override
    public void handle(@NotNull final PacketOutContextRole packet) {
        //TODO Verarbeitung des ContextRole-Pakets implementieren.
    }
}
