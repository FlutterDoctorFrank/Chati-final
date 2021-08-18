package model.communication.message;

import model.user.IUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    @Nullable IUser getSender();

    /**
     * Gibt den Zeitpunkt, an dem die Nachricht versendet wurde, zurück.
     * @return Zeitstempel der Nachricht.
     */
    @NotNull LocalDateTime getTimestamp();
}