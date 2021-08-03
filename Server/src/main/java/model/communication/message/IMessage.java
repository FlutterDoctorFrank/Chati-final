package model.communication.message;

import model.user.IUser;

import java.time.LocalDateTime;

/**
 * Ein Interface, welche dem Controller Methoden zur Verwaltung von Nachrichten bereitstellt. Wird von
 * {@link Message} implementiert.
 */
public interface IMessage {

    /**
     * Gibt den Sender der Nachricht zurück.
     * @return Sender der Nachricht.
     */
    public IUser getSender();

    /**
     * Gibt den Zeitpunkt, an dem die Nachricht versendet wurde, zurück.
     * @return Zeitstempel der Nachricht.
     */
    public LocalDateTime getTimestamp();
}