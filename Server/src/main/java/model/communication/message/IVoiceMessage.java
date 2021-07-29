package model.communication.message;

/**
 * Eine Schnittstelle, welche dem Controller Methoden zur Verwaltung von Sprachnachrichten bereitstellt. Wird von
 * {@link VoiceMessage} implementiert.
 */
public interface IVoiceMessage extends IMessage {

    /**
     * Gibt die Sprachdaten der Sprachnachricht zurück.
     * @return Enthaltene Sprachdaten.
     */
    public byte[] getVoiceData();
}
