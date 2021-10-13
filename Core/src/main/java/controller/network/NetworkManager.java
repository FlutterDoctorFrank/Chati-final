package controller.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import controller.network.protocol.PacketAudioMessage;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketChatMessage;
import controller.network.protocol.PacketInContextInteract;
import controller.network.protocol.PacketInUserManage;
import controller.network.protocol.PacketMenuOption;
import controller.network.protocol.PacketNotificationResponse;
import controller.network.protocol.PacketOutCommunicable;
import controller.network.protocol.PacketOutContextInfo;
import controller.network.protocol.PacketOutContextJoin;
import controller.network.protocol.PacketOutContextList;
import controller.network.protocol.PacketOutContextRole;
import controller.network.protocol.PacketOutMenuAction;
import controller.network.protocol.PacketOutNotification;
import controller.network.protocol.PacketOutUserInfo;
import controller.network.protocol.PacketProfileAction;
import controller.network.protocol.PacketUserTyping;
import controller.network.protocol.PacketVideoFrame;
import controller.network.protocol.PacketWorldAction;
import model.role.Permission;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDateTime;

/**
 * Eine abstrakte Klasse, die die Verbindung zwischen dem Client und dem Server verwaltet
 * und vor dem Datenaustausch alle benötigten ein- und ausgehende Pakete registriert.
 * @param <T> Der EndPoint-Typ einer Verbindung von KryoNet.
 */
public abstract class NetworkManager<T extends EndPoint> extends Listener {

    private static final String HOST_IP = "85.215.241.212"; // "81.169.218.151";
    private static final int HOST_TCP_PORT = 54777;
    private static final int HOST_UDP_PORT = 59001;

    protected static final int WRITE_SIZE = (int) Math.pow(2, 21);
    protected static final int READ_SIZE = (int) Math.pow(2, 18) + (int) Math.pow(2, 12);

    protected volatile boolean active;
    protected final T endPoint;

    protected String host;
    protected int tcp;
    protected int udp;

    protected NetworkManager(@NotNull final T endPoint) {
        this.endPoint = endPoint;
        this.host = HOST_IP;
        this.tcp = HOST_TCP_PORT;
        this.udp = HOST_UDP_PORT;
        this.register(endPoint.getKryo());

        endPoint.addListener(this);
    }

    /**
     * Registriert alle Netzwerkpakete die zwischen der Server- und der Client-Anwendung ausgetauscht werden.
     * @param kryo Die Kryo-Instanz, in der die Netzwerkpakete registriert werden.
     */
    private void register(@NotNull final Kryo kryo) {
        kryo.register(LocalDateTime.class);
        kryo.register(Permission.class);
        kryo.register(PacketAvatarMove.class);
        kryo.register(PacketAudioMessage.class);
        kryo.register(PacketChatMessage.class);
        kryo.register(PacketMenuOption.class);
        kryo.register(PacketNotificationResponse.class);
        kryo.register(PacketProfileAction.class);
        kryo.register(PacketUserTyping.class);
        kryo.register(PacketVideoFrame.class);
        kryo.register(PacketWorldAction.class);
        kryo.register(PacketInContextInteract.class);
        kryo.register(PacketInUserManage.class);
        kryo.register(PacketOutCommunicable.class);
        kryo.register(PacketOutContextInfo.class);
        kryo.register(PacketOutContextJoin.class);
        kryo.register(PacketOutContextList.class);
        kryo.register(PacketOutContextRole.class);
        kryo.register(PacketOutMenuAction.class);
        kryo.register(PacketOutNotification.class);
        kryo.register(PacketOutUserInfo.class);
    }

    /**
     * Gibt den Endpunkt zurück, der von dem Netzwerkmanager verwaltet wird.
     * @return der Endpunkt des Netzwerkmanagers.
     */
    public @NotNull T getEndPoint() {
        return this.endPoint;
    }

    /**
     * Setzt den Hostnamen bzw. die IP-Adresse, die von dem Netzwerkmanager genutzt wird.
     * @param host der Hostname oder die IP-Adresse.
     */
    public void setHost(@NotNull final String host) {
        this.host = host;
    }

    /**
     * Setzt den TCP- und UDP-Port, die von dem Netzwerkmanager genutzt werden.
     * @param tcp der neue TCP-Port der genutzt werden soll, oder -1.
     * @param udp der neue UDP-Port der genutzt werden soll, oder -1.
     */
    public void setPorts(final int tcp, final int udp) {
        this.tcp = tcp > 0 ? tcp : this.tcp;
        this.udp = udp > 0 ? udp : this.udp;
    }

    /**
     * Gibt zurück, ob der Netzwerkmanager momentan aktiv ist.
     * @return true wenn der Netzwerkmanager aktiv ist, ansonsten false.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Startet den Netzwerkmanager.
     */
    public abstract void start();

    /**
     * Stoppt den Netzwerkmanager.
     */
    public abstract void stop();
}
