package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.context.ContextID;
import model.context.spatial.Music;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ein Paket, das Informationen über die Musik innerhalb eines Kontexts enthält.
 * Das Paket wird vom Server erzeugt und an einen Client gesendet.
 * Das Paket teilt dem Client, die zu spielende Hintergrundmusik eines Kontextes, mit.
 */
public class PacketOutContextMusic implements Packet<PacketListenerOut> {

    private ContextID contextId;
    private Music music;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    public PacketOutContextMusic() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param contextId die ID des Kontexts in der die Musik gesetzt wird..
     * @param music die neue Musik die gesetzt werden soll, oder null wenn die Musik entfernt werden soll.
     */
    public PacketOutContextMusic(@NotNull final ContextID contextId, @Nullable final Music music) {
        this.contextId = contextId;
        this.music = music;
    }

    @Override
    public void call(@NotNull final PacketListenerOut listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeContextId(output, this.contextId);

        if (this.music != null) {
            output.writeBoolean(true);
            PacketUtils.writeEnum(output, this.music);
        } else {
            output.writeBoolean(false);
        }
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.contextId = PacketUtils.readContextId(input);

        if (input.readBoolean()) {
            this.music = PacketUtils.readEnum(input, Music.class);
        }
    }

    /**
     * Gibt die Kontext-ID des Kontexts zurück, in dem eine Musik abgespielt werden soll.
     * @return die ID des Kontexts.
     */
    public @NotNull ContextID getContextId() {
        return this.contextId;
    }

    /**
     * Gibt die Musik, die innerhalb des Kontexts abgespielt werden soll, zurück.
     * @return die neue Musik im Kontext.
     */
    public @Nullable Music getMusic() {
        return this.music;
    }
}
