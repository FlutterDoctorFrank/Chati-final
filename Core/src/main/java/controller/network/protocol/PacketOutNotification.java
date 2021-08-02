package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.MessageBundle;
import model.context.ContextID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Ein Paket, das Informationen über die Benachrichtigungen eines Benutzers enthält.
 * <p>
 *     Das Paket wird vom Server erzeugt und an einen Client gesendet.
 *     Das Paket teilt dem Client die erhaltenen Benachrichtigungen und Anfragen mit.
 * </p>
 */
public class PacketOutNotification implements Packet<PacketListenerOut> {

    /*
     * Da KryoNet momentan nur eine maximale Paketgröße von 512 Bytes erlaubt, werden über dieses Netzwerkpaket nur
     * eine einzelne Benachrichtigung gesendet.
     * Eine Benachrichtigung benötigt ungefähr ~70 Bytes. Werden mehrere Benachrichtigungen innerhalb eines
     * Netzwerkpakets versendet, so wird die maximale Paketgröße bereits nach wenigen Benachrichtigungen überschritten.
     */
    private Notification notification;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    public PacketOutNotification() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Server-Anwendung.
     * @param notification die Information über eine Benachrichtigung.
     */
    public PacketOutNotification(@NotNull final Notification notification) {
        this.notification = notification;
    }

    @Override
    public void call(@NotNull final PacketListenerOut listener) {

    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeUniqueId(output, this.notification.getNotificationId());
        PacketUtils.writeContextId(output, this.notification.getContextId());
        PacketUtils.writeBundle(output, this.notification.getMessage());
        kryo.writeObject(output, this.notification.getTimestamp());
        output.writeBoolean(this.notification.isRequest());
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.notification = new Notification(PacketUtils.readUniqueId(input), PacketUtils.readContextId(input),
                PacketUtils.readBundle(input), kryo.readObject(input, LocalDateTime.class), input.readBoolean());
    }

    /**
     * Gibt die Informationen über eine Benachrichtigung zurück.
     * @return die Informationen der Benachrichtigung.
     */
    public @NotNull Notification getNotification() {
        return this.notification;
    }

    /**
     * Eine Klasse, die die Informationen einer einzelnen Benachrichtigung enthält.
     */
    public static class Notification {

        public Notification(@NotNull final UUID notificationId, @NotNull final ContextID contextId,
                            @NotNull final MessageBundle message, @NotNull final LocalDateTime timestamp,
                            final boolean request) {
            this.notificationId = notificationId;
            this.contextId = contextId;
            this.message = message;
            this.timestamp = timestamp;
            this.request = request;
        }

        private final UUID notificationId;
        private final ContextID contextId;
        private final MessageBundle message;
        private final LocalDateTime timestamp;
        private final boolean request;

        /**
         * Gibt die ID der Benachrichtigung zurück.
         * @return die Benachrichtigungs-ID.
         */
        public @NotNull UUID getNotificationId() {
            return this.notificationId;
        }

        /**
         * Gibt die Kontext-ID des geltenden Kontexts der Benachrichtigung zurück.
         * @return die Kontext-ID, in dem die Benachrichtigung gilt.
         */
        public @NotNull ContextID getContextId() {
            return this.contextId;
        }

        /**
         * Gibt die übersetzbare Nachricht der Benachrichtigung zurück.
         * @return die Nachricht der Benachrichtigung.
         */
        public @NotNull MessageBundle getMessage() {
            return this.message;
        }

        /**
         * Gibt den Zeitpunkt, an dem die Benachrichtigung erstellt wurde, zurück.
         * @return der Zeitstempel der Benachrichtigung
         */
        public @NotNull LocalDateTime getTimestamp() {
            return this.timestamp;
        }

        /**
         * Gibt zurück, ob die Benachrichtigung eine Anfrage repräsentiert.
         * @return true, wenn die Benachrichtigung eine Anfrage ist, sonst false.
         */
        public boolean isRequest() {
            return this.request;
        }

        @Override
        public boolean equals(@Nullable final Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            return this.notificationId.equals(((Notification) object).notificationId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.notificationId);
        }
    }
}
