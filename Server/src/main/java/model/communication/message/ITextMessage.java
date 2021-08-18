package model.communication.message;

import model.MessageBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ein Interface, welche dem Controller Methoden zur Verwaltung von Textnachrichten bereitstellt. Wird von
 * {@link TextMessage} implementiert.
 */
public interface ITextMessage extends IMessage {

    /**
     * Gibt den Text der Nachricht zurück.
     * @return Enthaltene Textnachricht.
     */
    @Nullable String getTextMessage();

    /**
     * Gibt den Nachrichtentyp der Nachricht zurück.
     * @return Nachrichtentyp der Textnachricht.
     */
    @Nullable MessageBundle getMessageBundle();

    /**
     * Gibt den Schlüssel einer übersetzbaren Nachricht und deren Argumente zurück.
     * @return Den Nachrichtenschlüssel wenn die Nachricht einen enthält, sonst null.
     */
    @NotNull MessageType getMessageType();
}