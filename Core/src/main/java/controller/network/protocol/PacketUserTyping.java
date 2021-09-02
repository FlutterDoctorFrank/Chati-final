package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PacketUserTyping implements Packet<PacketListener> {

    private UUID senderId;

    /**
     * Für die Deserialisierung des Netzwerkpakets und Erzeugung des Netzwerkpakets von der Clientanwendung.
     */
    public PacketUserTyping() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Serveranwendung.
     * @param senderId die Benutzer-ID des tippenden Benutzers.
     */
    public PacketUserTyping(UUID senderId) {
        this.senderId = senderId;
    }

    @Override
    public void call(@NotNull final PacketListener listener) {
        listener.handle(this);
    }

    @Override
    public void write(Kryo kryo, Output output) {
        PacketUtils.writeNullableUniqueId(output, this.senderId);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        this.senderId = PacketUtils.readNullableUniqueId(input);
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{senderId=" + this.senderId + "}";
    }

    /**
     * Gibt die Benutzer-ID des tippenden Benutzers zurück.
     * @return die Benutzer-ID des tippenden Benutzers, oder null.
     */
    public @Nullable UUID getSenderId() {
        return this.senderId;
    }
}
