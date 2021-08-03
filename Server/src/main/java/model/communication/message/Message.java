package model.communication.message;

import model.user.User;

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
    public Message(User sender) {
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public User getSender() {
        return sender;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}