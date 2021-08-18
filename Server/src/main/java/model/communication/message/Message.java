package model.communication.message;

import model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;

/**
 * Eine Klasse, welche Nachrichten von Benutzern repräsentiert.
 */
public abstract class Message implements IMessage {

    /** Der Sender dieser Nachricht. */
    private final User sender;

    /** Der Zeitstempel dieser Nachricht. */
    private final LocalDateTime timestamp;

    /**
     * Erzeugt eine neue Instanz der Nachricht.
     * @param sender Der Sender dieser Nachricht.
     */
    public Message(@Nullable final User sender) {
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public @Nullable User getSender() {
        return sender;
    }

    @Override
    public @NotNull LocalDateTime getTimestamp() {
        return timestamp;
    }
}