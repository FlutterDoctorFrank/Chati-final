package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.context.ContextID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Ein Paket, das Informationen über die verfügbaren Welten oder über die privaten Räume
 * innerhalb einer Welt enthält.
 * <p>
 *     Das Paket wird vom Server erzeugt und an einen Client gesendet. Das Paket teilt dem
 *     Client die verfügbaren Welten bzw die verfügbaren privaten Räume mit.
 * </p>
 */
public class PacketOutContextList implements Packet<PacketListenerOut> {

    private ContextID contextId;
    private ContextInfo[] infos;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketOutContextList() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param contextId die ID des übergeordneten Kontexts.
     * @param infos die Informationen der untergeordneten Kontexte.
     */
    public PacketOutContextList(@Nullable final ContextID contextId, @NotNull final Collection<ContextInfo> infos) {
        this.contextId = contextId;
        this.infos = infos.toArray(new ContextInfo[0]);
    }

    @Override
    public void call(@NotNull final PacketListenerOut listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeNullableContextId(output, this.contextId);
        output.writeInt(this.infos.length, true);

        for (final ContextInfo info : this.infos) {
            PacketUtils.writeContextId(output, info.getContextId());
            output.writeString(info.getName());
            output.writeBoolean(info.isPrivate());
        }
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.contextId = PacketUtils.readNullableContextId(input);
        this.infos = new ContextInfo[input.readInt(true)];

        for (int index = 0; index < this.infos.length; index++) {
            this.infos[index] = new ContextInfo(PacketUtils.readContextId(input), input.readString(), input.readBoolean());
        }
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{contextId=" + this.contextId + ", infos=" +
                Arrays.toString(this.infos) + "}";
    }

    /**
     * Gibt die Kontext-ID des übergeordneten Kontexts zurück.
     * Enthält das Paket Informationen über die verfügbaren Welten, so wird null zurückgegeben.
     * Enthält das Paket Informationen über die verfügbaren privaten Räume, so wird die Kontext-ID der übergeordneten
     * Welt zurückgegeben.
     * @return die ID des übergeordneten Kontexts.
     */
    public @Nullable ContextID getContextId() {
        return contextId;
    }

    /**
     * Gibt ein Array mit den verfügbaren Welten bzw den verfügbaren privaten Räumen zurück.
     * @return die Informationen der untergeordneten Kontexte.
     */
    public @NotNull ContextInfo[] getInfos() {
        return infos;
    }

    /**
     * Eine Klasse, die die Informationen eines einzelnen Kontextes enthält.
     */
    public static class ContextInfo {

        private final ContextID contextId;
        private final String name;
        private final boolean isPrivate;

        public ContextInfo(@NotNull final ContextID contextId, @NotNull final String name, final boolean isPrivate) {
            this.contextId = contextId;
            this.name = name;
            this.isPrivate = isPrivate;
        }

        /**
         * Gibt die Kontext-ID der Welt bzw des privaten Raums zurück
         * @return die ID des Kontexts.
         */
        public @NotNull ContextID getContextId() {
            return this.contextId;
        }

        /**
         * Gibt den Namen der Welt bzw des privaten Raums zurück.
         * @return der Name des Kontexts.
         */
        public @NotNull String getName() {
            return this.name;
        }

        /**
         * Gibt zurück, ob der Raum privat ist. Im Fall einer Welt wird immer false zurückgegeben.
         * @return true, wenn der Raum privat ist, sonst false.
         */
        public boolean isPrivate() {
            return this.isPrivate;
        }

        @Override
        public @NotNull String toString() {
            return "{contextId=" + this.contextId + ", name='" + this.name + "', private=" + this.isPrivate + "}";
        }

        @Override
        public boolean equals(@Nullable final Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }

            final ContextInfo other = (ContextInfo) object;

            return this.contextId.equals(other.contextId) && this.name.equals(other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.contextId, this.name);
        }
    }
}
