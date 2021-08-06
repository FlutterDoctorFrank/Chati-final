package controller.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketInContextInteract;
import controller.network.protocol.PacketInUserManage;
import controller.network.protocol.PacketOutContextList;
import controller.network.protocol.PacketOutContextJoin;
import controller.network.protocol.PacketOutContextInfo;
import controller.network.protocol.PacketOutContextRole;
import controller.network.protocol.PacketProfileAction;
import controller.network.protocol.PacketWorldAction;
import org.jetbrains.annotations.NotNull;

/**
 * Eine abstrakte Klasse, die die Verbindung zwischen dem Client und dem Server verwaltet
 * und vor dem Datenaustausch alle benötigten ein- und ausgehende Pakete registriert.
 * @param <T> Der EndPoint-Typ einer Verbindung von KryoNet.
 */
public abstract class NetworkManager<T extends EndPoint> extends Listener {

    protected static final String HOST_IP = "127.0.0.1";
    protected static final int HOST_TCP_PORT = 54777;
    protected static final int HOST_UDP_PORT = 59001;

    protected final T endPoint;

    protected NetworkManager(@NotNull final T endPoint) {
        this.endPoint = endPoint;
        this.register(endPoint.getKryo());

        endPoint.addListener(this);
    }

    /**
     * Registriert alle Netzwerkpakete die zwischen der Server- und der Client-Anwendung ausgetauscht werden.
     * @param kryo Die Kryo-Instanz, in der die Netzwerkpakete registriert werden.
     */
    private void register(@NotNull final Kryo kryo) {
        kryo.register(PacketAvatarMove.class);
        kryo.register(PacketInContextInteract.class);
        kryo.register(PacketInUserManage.class);
        kryo.register(PacketOutContextList.class);
        kryo.register(PacketOutContextJoin.class);
        kryo.register(PacketOutContextInfo.class);
        kryo.register(PacketOutContextRole.class);
        kryo.register(PacketProfileAction.class);
        kryo.register(PacketWorldAction.class);
    }

    public @NotNull T getEndPoint() {
        return this.endPoint;
    }

    public abstract void start();

    public abstract void stop();
}
