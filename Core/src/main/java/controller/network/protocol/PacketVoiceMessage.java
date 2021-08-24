package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

/**
 * Ein Paket, das die Sprachdaten eines Benutzers enthält.
 * <p>
 *     Das Paket wird von einem Client mit den Sprachdaten erzeugt und an den Server gesendet.
 *     Nach der Verarbeitung des Pakets vom Server, wird der Sender gesetzt und das Paket wird an all die Clients
 *     verteilt, die die Sprachnachricht empfangen dürfen.
 * </p>
 */
public class PacketVoiceMessage implements Packet<PacketListener> {

    private UUID senderId;
    private LocalDateTime timestamp;
    private byte[] voiceData;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketVoiceMessage() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Client-Anwendung.
     * @param voiceData die eingesprochenen Sprachdaten des Benutzers.
     */
    public PacketVoiceMessage(final byte[] voiceData) {
        this.voiceData = voiceData;
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param senderId die Benutzer-ID des Senders der Sprachnachricht.
     * @param timestamp der Zeitpunkt, an dem die Sprachnachricht versendet wurde.
     * @param voiceData die eingesprochenen Sprachdaten des Benutzers.
     */
    public PacketVoiceMessage(@NotNull final UUID senderId, @NotNull final LocalDateTime timestamp,
                              final byte[] voiceData) {
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.voiceData = voiceData;
    }

    @Override
    public void call(@NotNull final PacketListener listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeNullableUniqueId(output, this.senderId);
        kryo.writeObjectOrNull(output, this.timestamp, LocalDateTime.class);
        output.writeVarInt(this.voiceData.length, true);
        output.writeBytes(this.voiceData);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.senderId = PacketUtils.readNullableUniqueId(input);
        this.timestamp = kryo.readObjectOrNull(input, LocalDateTime.class);
        this.voiceData = input.readBytes(input.readVarInt(true));
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{senderId=" + this.senderId + ", timestamp=" + this.timestamp +
                ", voiceData=" + Arrays.toString(this.voiceData) + "}";
    }

    /**
     * Gibt die Benutzer-ID des Senders der Sprachnachricht zurück.
     * @return die Benutzer-ID des Senders, oder null.
     */
    public @Nullable UUID getSenderId() {
        return this.senderId;
    }

    /**
     * Gibt den Zeitpunkt, an dem die Sprachnachricht gesendet wurde, zurück.
     * @return der Zeitstempel der Sprachnachricht.
     */
    public @Nullable LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    /**
     * Gibt die eingesprochenen Sprachdaten des Benutzers zurück.
     * @return die Sprachdaten des Benutzers.
     */
    public byte[] getVoiceData() {
        return this.voiceData;
    }
}
