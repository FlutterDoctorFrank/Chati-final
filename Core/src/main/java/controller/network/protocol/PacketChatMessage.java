package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.MessageBundle;
import model.communication.message.MessageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Ein Paket, das Informationen über eine Chat-Nachricht eines Benutzers enthält.
 * <p>
 *     Das Paket wird von einem Client mit der Nachricht erzeugt und an den Server gesendet.
 *     Nach der Verarbeitung des Pakets vom Server, wird der Sender, der Nachrichten-Typ und der Zeitstempel gesetzt
 *     und das Paket wird an all die Clients verteilt, die die Nachricht empfangen dürfen.
 * </p>
 */
public class PacketChatMessage implements Packet<PacketListener> {

    private MessageBundle bundle;
    private MessageType type;

    private UUID senderId;
    private String message;
    private LocalDateTime timestamp;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    public PacketChatMessage() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Client-Anwendung.
     * @param message die eingegebene Nachricht des Benutzers.
     */
    public PacketChatMessage(@NotNull final String message) {
        this.message = message;
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets zum Senden einer übersetzbaren Information-Nachricht von der
     * Server-Anwendung.
     * @param bundle das message bundle, die die Informations-Nachricht repräsentiert.
     * @param timestamp der Zeitpunkt, an dem die Informations-Nachricht versendet wurde.
     */
    public PacketChatMessage(@NotNull final MessageBundle bundle, @NotNull final LocalDateTime timestamp) {
        this.bundle = bundle;
        this.type = MessageType.INFO;
        this.timestamp = timestamp;
    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * <p><i>
     *     Dieser Konstruktor erlaubt nicht die Erzeugung des ChatInfo-Pakets. Siehe
     *     {@link PacketChatMessage(MessageBundle, LocalDateTime)}
     * </i></p>
     * @param type der Nachrichten-Typ der zur übermittelten Nachricht.
     * @param senderId die Benutzer-ID des Senders der Nachricht.
     * @param message die Nachricht des Senders.
     * @param timestamp der Zeitpunkt, an dem der Server die Nachricht gesendet hat.
     */
    public PacketChatMessage(@NotNull final MessageType type, @NotNull final UUID senderId,
                             @NotNull final String message, @NotNull final LocalDateTime timestamp) {
        if (type == MessageType.INFO) {
            throw new IllegalArgumentException("Invalid Constructor for creating InfoMessage-Packets");
        }

        this.type = type;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public void call(@NotNull final PacketListener listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeNullableEnum(output, this.type);
        PacketUtils.writeNullableBundle(output, this.bundle);
        PacketUtils.writeNullableUniqueId(output, this.senderId);
        output.writeString(this.message);
        kryo.writeObjectOrNull(output, this.timestamp, LocalDateTime.class);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.type = PacketUtils.readNullableEnum(input, MessageType.class);
        this.bundle = PacketUtils.readNullableBundle(input);
        this.senderId = PacketUtils.readNullableUniqueId(input);
        this.message = input.readString();
        this.timestamp = kryo.readObjectOrNull(input, LocalDateTime.class);
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{type=" + this.type + ", senderId=" + this.senderId + ", message='"
                + this.message + "', timestamp=" + this.timestamp + ", bundle=" + this.bundle + "}";
    }

    /**
     * Gibt die übersetzbare Nachrichten zurück, falls es sich um den Nachrichten-Typ {@link MessageType#INFO} handelt.
     * @return die übersetzbare Informations-Nachricht, oder null.
     */
    public @Nullable MessageBundle getBundle() {
        return this.bundle;
    }

    /**
     * Gibt den Nachrichten-Typ der Nachricht zurück.
     * @return den Nachrichten-Typ der Nachricht, oder null.
     */
    public @Nullable MessageType getMessageType() {
        return this.type;
    }

    /**
     * Gibt die Benutzer-ID des Senders der Nachricht zurück.
     * @return die Benutzer-ID des Senders.
     */
    public @Nullable UUID getSenderId() {
        return this.senderId;
    }

    /**
     * Gibt die vom Sender gesendete Nachricht zurück.
     * @return die Nachricht des Senders.
     */
    public @Nullable String getMessage() {
        return this.message;
    }

    /**
     * Gibt den Zeitpunkt, an dem die Nachricht gesendet wurde, zurück.
     * @return der Zeitstempel der Nachricht.
     */
    public @Nullable LocalDateTime getTimestamp() {
        return this.timestamp;
    }
}
