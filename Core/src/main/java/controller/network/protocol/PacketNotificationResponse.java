package controller.network.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import model.notification.NotificationAction;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

/**
 * Ein Paket, das Informationen über die Verwaltungsmöglichkeit einer Benachrichtigung enthält.
 * <p>
 *     Das Paket wird von einem Client erzeugt und an den Server gesendet.
 *     Das Paket teilt dem Server mit, dass der Benutzer eine Benachrichtigung gelöscht hat oder auf eine Anfrage
 *     reagiert hat.
 * </p>
 */
public class PacketNotificationResponse implements Packet<PacketListener> {

    private UUID notificationId;
    private NotificationAction action;

    /**
     * @deprecated Ausschließlich für die Deserialisierung des Netzwerkpakets.
     */
    @Deprecated
    public PacketNotificationResponse() {

    }

    /**
     * Ausschließlich für die Erzeugung des Netzwerkpakets von der Client-Anwendung.
     * @param notificationId die ID der Benachrichtigung auf der eine Aktion ausgeführt werden soll.
     * @param action die Aktion die auf die Benachrichtigung ausgeführt werden soll.
     */
    public PacketNotificationResponse(@NotNull final UUID notificationId, @NotNull final NotificationAction action) {
        this.notificationId = notificationId;
        this.action = action;
    }

    @Override
    public void call(@NotNull final PacketListener listener) {
        listener.handle(this);
    }

    @Override
    public void write(@NotNull final Kryo kryo, @NotNull final Output output) {
        PacketUtils.writeUniqueId(output, this.notificationId);
        PacketUtils.writeEnum(output, this.action);
    }

    @Override
    public void read(@NotNull final Kryo kryo, @NotNull final Input input) {
        this.notificationId = PacketUtils.readUniqueId(input);
        this.action = PacketUtils.readEnum(input, NotificationAction.class);
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "{notificationId=" + this.notificationId + ", action=" + this.action + "}";
    }

    /**
     * Gibt die ID der Benachrichtigung zurück.
     * @return die ID der Benachrichtigung.
     */
    public @NotNull UUID getNotificationId() {
        return this.notificationId;
    }

    /**
     * Gibt die Aktion, die auf der Benachrichtigung ausgeführt werden soll, zurück.
     * @return die auszuführende Aktion.
     */
    public @NotNull NotificationAction getAction() {
        return this.action;
    }
}
