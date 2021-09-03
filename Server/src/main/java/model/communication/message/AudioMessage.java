package model.communication.message;

import model.user.User;
import org.jetbrains.annotations.Nullable;

/**
 * Eine Klasse, welche Audiodaten repräsentiert.
 */
public class AudioMessage extends Message implements IAudioMessage {

    /** Die Audiodaten der Sprachnachricht. */
    private final byte[] audioData;

    /**
     * Erzeugt eine neue Instanz der Audionachricht.
     * @param sender Der Sender dieser Nachricht.
     * @param audioData Die Audiodaten der Nachricht.
     */
    public AudioMessage(@Nullable final User sender, final byte[] audioData) {
        super(sender);
        this.audioData = audioData;
    }

    @Override
    public byte[] getAudioData() {
        return audioData;
    }
}