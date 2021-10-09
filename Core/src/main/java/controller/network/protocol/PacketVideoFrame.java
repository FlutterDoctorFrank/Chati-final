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
 * Ein Paket, das die Videodaten eines Benutzers enthält.
 * <p>
 *     Das Paket wird von einem Client mit den Videodaten erzeugt und an den Server gesendet.
 *     Nach der Verarbeitung des Pakets vom Server, wird der Sender gesetzt und das Paket wird an all die Clients
 *     verteilt, die das Frame empfangen dürfen.
 * </p>
 */
public class PacketVideoFrame implements Packet<PacketListener> {

    private UUID senderId;
    private LocalDateTime timestamp;
    private byte[] frameData;
    private int number;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketVideoFrame() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Client-Anwendung.
     * @param frameData die Daten des Frames von der Kameraaufzeichnung des Benutzers.
     */
    public PacketVideoFrame(final byte[] frameData, final int number) {
        this.frameData = frameData;
        this.number = number;
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param senderId die Benutzer-ID des Senders des Videoframes.
     * @param timestamp der Zeitpunkt, an dem das Frame versendet wurde.
     * @param frameData die Daten des Frames von der Kameraaufzeichnung des Benutzers.
     */
    public PacketVideoFrame(@Nullable final UUID senderId, @NotNull final LocalDateTime timestamp,
                              final byte[] frameData, final int number) {
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.frameData = frameData;
        this.number = number;
    }

    @Override
    public void call(@NotNull final PacketListener listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeNullableUniqueId(output, this.senderId);
        kryo.writeObjectOrNull(output, this.timestamp, LocalDateTime.class);
        output.writeVarInt(this.frameData.length, true);
        output.writeBytes(this.frameData);
        output.writeVarInt(number, true);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.senderId = PacketUtils.readNullableUniqueId(input);
        this.timestamp = kryo.readObjectOrNull(input, LocalDateTime.class);
        this.frameData = input.readBytes(input.readVarInt(true));
        this.number = input.readVarInt(true);
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{senderId=" + this.senderId + ", timestamp=" + this.timestamp
                + ", frameData=" + Arrays.toString(this.frameData) + ", number=" + this.number + "}";
    }

    /**
     * Gibt die Benutzer-ID des Senders des Frames zurück.
     * @return die Benutzer-ID des Senders, oder null.
     */
    public @Nullable UUID getSenderId() {
        return this.senderId;
    }

    /**
     * Gibt den Zeitpunkt, an dem das Frame gesendet wurde, zurück.
     * @return der Zeitstempel des Frames.
     */
    public @Nullable LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    /**
     * Gibt die Daten des Frames von der Kameraaufzeichnung des Benutzers zurück.
     * @return die Daten des Frames von der Kameraaufzeichnung des Benutzers.
     */
    public byte[] getFrameData() {
        return this.frameData;
    }

    /**
     * Gibt die Nummer des Pakets zurück.
     * @return Nummer des Pakets.
     */
    public int getNumber() {
        return this.number;
    }
}
