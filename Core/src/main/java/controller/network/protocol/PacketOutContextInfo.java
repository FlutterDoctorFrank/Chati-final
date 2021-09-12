package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.context.ContextID;
import model.context.spatial.ContextMusic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

/**
 * Ein Paket, das Informationen über die im Kontext stumm geschalteten Benutzer und die geltende Musik enthält.
 * <p>
 *     Das Paket wird vom Server erzeugt und an einen Client gesendet.
 *     Das Paket teilt dem Client, die stumm geschalteten Benutzer und die zu spielende Hintergrundmusik eines
 *     Kontextes, mit.
 * </p>
 */
public class PacketOutContextInfo implements Packet<PacketListenerOut> {

    private ContextID contextId;
    private ContextMusic music;
    private UUID[] mutes;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketOutContextInfo() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param contextId die ID des Kontexts zu dem die Information gehört.
     * @param music die neue Musik im Kontext, oder null, wenn dort keine Musik läuft.
     * @param mutes die im Kontext stumm geschalteten Benutzer.
     */
    public PacketOutContextInfo(@NotNull final ContextID contextId, @Nullable final ContextMusic music,
                                @NotNull final Collection<UUID> mutes) {
        this.contextId = contextId;
        this.music = music;
        this.mutes = mutes.toArray(new UUID[0]);
    }

    @Override
    public void call(@NotNull final PacketListenerOut listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeContextId(output, this.contextId);
        PacketUtils.writeNullableEnum(output, this.music);
        output.writeVarInt(this.mutes.length, true);

        for (final UUID mute : this.mutes) {
            PacketUtils.writeUniqueId(output, mute);
        }
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.contextId = PacketUtils.readContextId(input);
        this.music = PacketUtils.readNullableEnum(input, ContextMusic.class);
        this.mutes = new UUID[input.readVarInt(true)];

        for (int index = 0; index < this.mutes.length; index++) {
            this.mutes[index] = PacketUtils.readUniqueId(input);
        }
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{contextId=" + this.contextId + ", mutes="
                + Arrays.toString(this.mutes) + ", music=" + this.music + "}";
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
    public @Nullable ContextMusic getMusic() {
        return this.music;
    }

    /**
     * Gibt die Benutzer zurück, die in diesem Kontext stumm geschaltet sind.
     * @return die stumm geschalteten Benutzer.
     */
    public @NotNull UUID[] getMutes() {
        return mutes;
    }
}
