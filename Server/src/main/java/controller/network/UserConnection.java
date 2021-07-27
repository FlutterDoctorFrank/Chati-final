package controller.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import controller.network.protocol.Packet;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketAvatarMove.AvatarAction;
import controller.network.protocol.PacketInContextInteract;
import controller.network.protocol.PacketListener;
import controller.network.protocol.PacketListenerIn;
import model.exception.IllegalActionException;
import model.exception.IllegalInteractionException;
import model.exception.IllegalPositionException;
import model.user.IUser;
import org.jetbrains.annotations.NotNull;

/**
 * Empfängt und verarbeitet alle eingehenden Pakete eines einzelnen Clients und sendet Pakete an diesen Client.
 */
public class UserConnection extends Listener implements PacketListenerIn, ClientSender {

    private final ServerNetworkManager manager;
    private final Connection connection;

    private IUser user;

    public UserConnection(@NotNull final ServerNetworkManager manager, @NotNull final Connection connection) {
        this.manager = manager;
        this.connection = connection;

        connection.addListener(this);
    }

    public void send(@NotNull final Packet<?> packet) {
        if (this.connection.isConnected()) {
            this.connection.sendTCP(packet);
        }
    }

    @Override
    public void send(@NotNull final SendAction action, @NotNull final Object object) {
        this.send(action.getPacket(this.user, object));
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

    @Override
    public void handle(@NotNull final PacketAvatarMove packet) {
        // Die Client-Anwendung darf nur die Aktion UPDATE_AVATAR versenden.
        if (packet.getAction() == AvatarAction.UPDATE_AVATAR) {
            // Überprüfung ob gegebenenfalls eine falsche User-ID versendet wurde.
            if (packet.getUserId() != null && !packet.getUserId().equals(this.user.getUserID())) {
                return;
            }

            try {
                this.user.move(packet.getPosX(), packet.getPosY());
            } catch (IllegalPositionException | IllegalActionException ex) {
                // Illegale Position. Sende vorherige Position.
                int posX = this.user.getLocation().getPosX();
                int posY = this.user.getLocation().getPosY();

                this.send(new PacketAvatarMove(AvatarAction.UPDATE_AVATAR, this.user.getUserID(), posX, posY));
            }
        }
    }

    @Override
    public void handle(@NotNull final PacketInContextInteract packet) {
        try {
            this.user.interact(packet.getContextId());
        } catch (IllegalInteractionException ex) {
            // Objekt wurde nicht gefunden oder der Benutzer interagiert bereits mit einem Objekt.
            // Verbindung trennen?
        }
    }
}
