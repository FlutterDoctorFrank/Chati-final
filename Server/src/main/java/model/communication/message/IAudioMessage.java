package model.communication.message;

/**
 * Ein Interface, welche dem Controller Methoden zur Verwaltung von Sprachnachrichten bereitstellt. Wird von
 * {@link AudioMessage} implementiert.
 */
public interface IAudioMessage extends IMessage {

    /**
     * Gibt die Sprachdaten der Sprachnachricht zurück.
     * @return Enthaltene Sprachdaten.
     */
    byte[] getAudioData();
}