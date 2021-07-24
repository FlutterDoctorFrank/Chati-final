package controller.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Abstrakte Klasse, die die Verbindung zwischen dem Client und dem Server verwaltet
 * und vor dem Datenaustausch alle benötigten ein- und ausgehende Pakete registriert.
 * @param <T> Der EndPoint-Typ einer Verbindung von KryoNet.
 */
public abstract class NetworkManager<T extends EndPoint> extends Listener {

    protected final T endPoint;

    protected NetworkManager(@NotNull final T endPoint) {
        this.endPoint = endPoint;
        this.register(endPoint.getKryo());

        endPoint.addListener(this);
    }

    /**
     * Registriert alle Netzwerkpakete die zwischen der Server- und der Client-Anwendung ausgetauscht werden.
     * @param kryo Die Kryo-Instanz in der die Netzwerkpakete registriert werden.
     */
    private void register(@NotNull final Kryo kryo) {

    }

    public @NotNull T getEndPoint() {
        return this.endPoint;
    }
}
