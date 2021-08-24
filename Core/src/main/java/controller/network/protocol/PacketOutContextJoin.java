package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.context.ContextID;
import model.context.spatial.SpatialMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ein Paket, das Informationen über den Beitritt eines Kontexts enthält.
 * <p>
 *     Das Paket wird vom Server erzeugt und an einen Client gesendet. Das Paket teilt dem
 *     Client beim Betreten einer Welt oder eines Raums mit, das eine neue Karte geladen werden soll.
 *     Das Paket wird zusätzlich genutzt, um einem Client mitzuteilen, das keine Karte angezeigt werden soll.
 * </p>
 */
public class PacketOutContextJoin implements Packet<PacketListenerOut> {

    private ContextID contextId;
    private String name;
    private SpatialMap map;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketOutContextJoin() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param contextId die ID des Kontexts der Karte, die geladen werden soll.
     * @param map die Karte die geladen werden soll, oder null, wenn die Karte verlassen werden soll.
     */
    public PacketOutContextJoin(@NotNull final ContextID contextId, @Nullable final String name,
                                @Nullable final SpatialMap map) {
        this.contextId = contextId;
        this.name = name;
        this.map = map;
    }

    @Override
    public void call(@NotNull final PacketListenerOut listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeContextId(output, this.contextId);
        output.writeString(this.name);
        PacketUtils.writeNullableEnum(output, this.map);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.contextId = PacketUtils.readContextId(input);
        this.name = input.readString();
        this.map = PacketUtils.readNullableEnum(input, SpatialMap.class);
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{contextId=" + this.contextId + ", name='" + this.name +
                "', map=" + this.map + "}";
    }

    /**
     * Gibt die Kontext-ID der Welt bzw des Raumes zurück.
     * @return die ID des Kontexts.
     */
    public @NotNull ContextID getContextId() {
        return this.contextId;
    }

    /**
     * Gibt den Namen der Welt bzw des Raumes zurück.
     * @return der Name des Kontexts.
     */
    public @Nullable String getName() {
        return this.name;
    }

    /**
     * Gibt die Karte der Welt bzw des Raumes zurück.
     * @return die Karte, sofern eine geladen werden soll.
     */
    public @Nullable SpatialMap getMap() {
        return this.map;
    }

    /**
     * Gibt zurück, ob die Welt bzw der Raum betreten und somit geladen werden soll.
     * @return true, wenn die Karte geladen werden soll.
     */
    public boolean isJoin() {
        return this.map != null;
    }
}
