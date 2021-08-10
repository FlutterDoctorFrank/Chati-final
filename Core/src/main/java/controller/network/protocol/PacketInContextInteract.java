package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.context.ContextID;
import org.jetbrains.annotations.NotNull;

/**
 * Ein Paket, das Informationen über die Interaktion eines Avatars mit einem Kontext enthält.
 * <p>
 *     Das Paket wird von einem Client erzeugt und an den Server gesendet. Das Paket teilt dem Server mit, dass der
 *     Benutzer mit einem Bereich oder einem Objekt interagieren möchte.
 * </p>
 */
public class PacketInContextInteract implements Packet<PacketListenerIn> {

    private ContextID contextId;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    public PacketInContextInteract() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Client-Anwendung.
     * @param contextId die ID des Kontexts mit dem interagiert werden soll.
     */
    public PacketInContextInteract(@NotNull final ContextID contextId) {
        this.contextId = contextId;
    }

    @Override
    public void call(@NotNull final PacketListenerIn listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeContextId(output, this.contextId);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.contextId = PacketUtils.readContextId(input);
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{contextId=" + this.contextId + "}";
    }

    /**
     * Gibt die Kontext-ID des Kontexts, mit dem interagiert wurde, zurück.
     * @return die ID des Kontexts.
     */
    public @NotNull ContextID getContextId() {
        return this.contextId;
    }
}
