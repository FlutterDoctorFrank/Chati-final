package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

/**
 * Ein Paket, das Informationen über Benutzer, mit denen kommuniziert werden kann, enthält.
 * <p>
 *     Das Paket wird vom Server erzeugt und an einen Client gesendet.
 *     Das Paket teilt dem Client die Benutzer mit, mit denen der Benutzer über die Kommunikationsmedien kommuniziert.
 * </p>
 */
public class PacketOutCommunicable implements Packet<PacketListenerOut> {

    private UUID[] communicables;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketOutCommunicable() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param communicables die IDs der Benutzer mit denen kommuniziert werden kann.
     */
    public PacketOutCommunicable(@NotNull final Collection<UUID> communicables) {
        this.communicables = communicables.toArray(new UUID[0]);
    }

    @Override
    public void call(@NotNull final PacketListenerOut listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        output.writeVarInt(this.communicables.length, true);

        for (final UUID communicable : communicables) {
            PacketUtils.writeUniqueId(output, communicable);
        }
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.communicables = new UUID[input.readVarInt(true)];

        for (int index = 0; index < this.communicables.length; index++) {
            this.communicables[index] = PacketUtils.readUniqueId(input);
        }
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{communicables=" + Arrays.toString(this.communicables) + "}";
    }

    /**
     * Gibt die IDs der Benutzer zurück, mit denen kommuniziert werden kann.
     * @return die Menge der kommunizierbaren Benutzer.
     */
    public @NotNull UUID[] getCommunicables() {
        return this.communicables;
    }
}
