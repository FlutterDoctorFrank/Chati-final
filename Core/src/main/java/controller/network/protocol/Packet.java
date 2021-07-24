package controller.network.protocol;

import com.esotericsoftware.kryo.KryoSerializable;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Schnittstelle, welche den grundsätzlichen Aufbau eines Pakets, das über das Netzwerk verschickt wird,
 * repräsentiert.
 * @param <T> Der PacketListener-Typ, welcher zum Verarbeiten des Packets aufgerufen werden wird.
 */
public interface Packet<T extends PacketListener> extends KryoSerializable {

    /**
     * Ruft den PacketListener auf und übergibt sich selbst, um die Verarbeitung des
     * Pakets anzustoßen.
     * @param listener Die Instanz des PacketListeners, der aufgerufen wird.
     */
    void call(@NotNull final T listener);
}
