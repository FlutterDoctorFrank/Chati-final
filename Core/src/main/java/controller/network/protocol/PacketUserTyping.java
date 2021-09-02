package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

/**
 * Ein Paket, das die Information enthält, ob ein Benutzer gerade tippt.
 * <p>
 *     Das Paket wird von einem Client erzeugt und an den Server gesendet.
 *     Nach der Verarbeitung des Pakets vom Server, wird der Sender gesetzt und das Paket wird an all die Clients
 *     verteilt, die die Information empfangen dürfen.
 * </p>
 */
public class PacketUserTyping implements Packet<PacketListener> {

    private UUID senderId;

    /**
     * Für die Deserialisierung des Netzwerkpakets und Erzeugung des Netzwerkpakets von der Client-Anwendung.
     */
    public PacketUserTyping() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param senderId die Benutzer-ID des tippenden Benutzers.
     */
    public PacketUserTyping(@NotNull final UUID senderId) {
        this.senderId = senderId;
    }

    @Override
    public void call(@NotNull final PacketListener listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeNullableUniqueId(output, this.senderId);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.senderId = PacketUtils.readNullableUniqueId(input);
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{senderId=" + this.senderId + "}";
    }

    /**
     * Gibt die Benutzer-ID des tippenden Benutzers zurück.
     * @return die Benutzer-ID des Benutzers, oder null.
     */
    public @Nullable UUID getSenderId() {
        return this.senderId;
    }
}
