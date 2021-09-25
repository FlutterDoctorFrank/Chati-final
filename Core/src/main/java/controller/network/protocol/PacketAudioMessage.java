package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.context.spatial.ContextMusic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

/**
 * Ein Paket, das die Sprachdaten eines Benutzers oder die Audiodaten eines Musikstückes enthält.
 * <p>
 *     Das Paket wird von einem Client mit den Sprachdaten erzeugt und an den Server gesendet.
 *     Nach der Verarbeitung des Pakets vom Server, wird der Sender gesetzt und das Paket wird an all die Clients
 *     verteilt, die die Sprachnachricht empfangen dürfen.
 *     Zusätzlich wird das Paket vom Server erzeugt um die Audiodaten von Musikstücken an die Clients zu verteilen.
 * </p>
 */
public class PacketAudioMessage implements Packet<PacketListener> {

    private UUID senderId;
    private LocalDateTime timestamp;
    private byte[] audioData;
    private ContextMusic music;
    private float position;
    private int seconds;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketAudioMessage() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Client-Anwendung.
     * @param audioData die eingesprochenen Sprachdaten des Benutzers.
     */
    public PacketAudioMessage(final byte[] audioData) {
        this.audioData = audioData;
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param timestamp der Zeitpunkt, an dem die Audiodaten versendet wurden.
     * @param audioData die Audiodaten des Musikstückes.
     * @param music die zugehörige Musik der Audiodaten.
     * @param position die Position im Musikstück.
     * @param seconds die Sekunde im Musikstück.
     */
    public PacketAudioMessage(@NotNull final LocalDateTime timestamp, @Nullable final ContextMusic music,
                              final byte[] audioData, final float position, final int seconds) {
        this.timestamp = timestamp;
        this.audioData = audioData;
        this.music = music;
        this.position = position;
        this.seconds = seconds;
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param senderId die Benutzer-ID des Senders der Sprachnachricht.
     * @param timestamp der Zeitpunkt, an dem die Sprachnachricht versendet wurde.
     * @param audioData die eingesprochenen Sprachdaten des Benutzers.
     */
    public PacketAudioMessage(@Nullable final UUID senderId, @NotNull final LocalDateTime timestamp,
                              final byte[] audioData) {
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.audioData = audioData;
    }

    @Override
    public void call(@NotNull final PacketListener listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeNullableUniqueId(output, this.senderId);
        PacketUtils.writeNullableEnum(output, this.music);
        kryo.writeObjectOrNull(output, this.timestamp, LocalDateTime.class);
        output.writeFloat(this.position);
        output.writeVarInt(this.seconds, true);
        output.writeVarInt(this.audioData.length, true);
        output.writeBytes(this.audioData);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.senderId = PacketUtils.readNullableUniqueId(input);
        this.music = PacketUtils.readNullableEnum(input, ContextMusic.class);
        this.timestamp = kryo.readObjectOrNull(input, LocalDateTime.class);
        this.position = input.readFloat();
        this.seconds = input.readVarInt(true);
        this.audioData = input.readBytes(input.readVarInt(true));
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{senderId=" + this.senderId + ", music=" + this.music +
                ", timestamp=" + this.timestamp + ",position=" + this.position + ",seconds="
                + this.seconds + ", audioData=" + Arrays.toString(this.audioData) + "}";
    }

    /**
     * Gibt die Benutzer-ID des Senders der Sprachnachricht zurück.
     * @return die Benutzer-ID des Senders, oder null.
     */
    public @Nullable UUID getSenderId() {
        return this.senderId;
    }

    /**
     * Gibt die zugehörige Musik der Audiodaten zurück.
     * @return Musik der Audiodaten, oder null.
     */
    public @Nullable ContextMusic getMusic() {
        return this.music;
    }

    /**
     * Gibt den Zeitpunkt, an dem die Sprachnachricht gesendet wurde, zurück.
     * @return der Zeitstempel der Sprachnachricht.
     */
    public @Nullable LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    /**
     * Gibt die eingesprochenen Sprachdaten des Benutzers oder die Audiodaten eines Musikstückes zurück.
     * @return die Sprachdaten des Benutzers oder die Audiodaten.
     */
    public byte[] getAudioData() {
        return this.audioData;
    }

    /**
     * Gibt die Position der Audiodaten im Musikstück zurück, falls es sich um die Audiodaten eines Musikstückes handelt.
     * @return die Position im Musikstück.
     */
    public float getPosition() {
        return this.position;
    }

    /**
     * Gibt die Sekunde der Audiodaten im Musikstück zurück, falls es sich um die Audiodaten eines Musikstückes handelt.
     * @return die Sekunde im Musikstück.
     */
    public int getSeconds() {
        return this.seconds;
    }
}
