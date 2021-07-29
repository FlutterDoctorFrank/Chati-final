package model.communication.message;

import model.user.User;

/**
 * Eine Klasse, welche Sprachnachrichten von Benutzern repräsentiert.
 */
public class VoiceMessage extends Message implements IVoiceMessage {

    /** Die Sprachdaten der Sprachnachricht. */
    private final byte[] voiceData;

    /**
     * Erzeugt eine neue Instanz der Sprachnachricht
     * @param sender Der Sender dieser Nachricht.
     * @param voiceData Die Sprachdaten der Nachricht.
     */
    public VoiceMessage(User sender, byte[] voiceData) {
        super(sender);
        this.voiceData = voiceData;
    }

    @Override
    public byte[] getVoiceData() {
        return voiceData;
    }
}
