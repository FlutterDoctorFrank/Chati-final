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
    private boolean screenshot;
    private byte[] frameData;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketVideoFrame() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Client-Anwendung.
     * @param screenshot falls true, ist dieses Frame von einer Bildschirmaufnahme, sonst von einer Kameraaufnahme.
     * @param frameData die Daten des Frames von der Kameraaufzeichnung des Benutzers.
     */
    public PacketVideoFrame(final boolean screenshot, final byte[] frameData) {
        this.screenshot = screenshot;
        this.frameData = frameData;
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param senderId die Benutzer-ID des Senders des Videoframes.
     * @param timestamp der Zeitpunkt, an dem das Frame versendet wurde.
     * @param screenshot falls true, ist dieses Frame von einer Bildschirmaufnahme, sonst von einer Kameraaufnahme.
     * @param frameData die Daten des Frames von der Kameraaufzeichnung des Benutzers.
     */
    public PacketVideoFrame(@Nullable final UUID senderId, @NotNull final LocalDateTime timestamp,
                            final boolean screenshot, final byte[] frameData) {
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.screenshot = screenshot;
        this.frameData = frameData;
    }

    @Override
    public void call(@NotNull final PacketListener listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeNullableUniqueId(output, this.senderId);
        kryo.writeObjectOrNull(output, this.timestamp, LocalDateTime.class);
        output.writeBoolean(screenshot);
        output.writeVarInt(this.frameData.length, true);
        output.writeBytes(this.frameData);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.senderId = PacketUtils.readNullableUniqueId(input);
        this.timestamp = kryo.readObjectOrNull(input, LocalDateTime.class);
        this.screenshot = input.readBoolean();
        this.frameData = input.readBytes(input.readVarInt(true));
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{senderId=" + this.senderId + ", timestamp=" + this.timestamp
                + ", isScreen=" + screenshot + ", frameData=" + Arrays.toString(this.frameData) + "}";
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
     * Gibt an, ob dieses Frame von einer Bildschirmaufnahme oder einer Kameraaufnahme ist.
     * @return true, falls dieses Frame von einer Bildschirmaufnahme ist, sonst false.
     */
    public boolean isScreenshot() {
        return this.screenshot;
    }

    /**
     * Gibt die Daten des Frames zurück.
     * @return die Daten des Frames.
     */
    public byte[] getFrameData() {
        return this.frameData;
    }
}
