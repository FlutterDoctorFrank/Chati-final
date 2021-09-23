package model.communication.message;

import model.user.User;
import org.jetbrains.annotations.Nullable;

/**
 * Eine Klasse, welche Audiodaten repräsentiert.
 */
public class AudioMessage extends Message implements IAudioMessage {

    /** Die Audiodaten der Sprachnachricht. */
    private final byte[] audioData;

    /** Die aktuelle Position in einer zusammenhängenden Audionachricht. */
    private final float position;

    /**
     * Erzeugt eine neue Instanz der Audionachricht.
     * @param sender Der Sender dieser Nachricht.
     * @param audioData Die Audiodaten der Nachricht.
     * @param position Aktuelle Position
     */
    public AudioMessage(@Nullable final User sender, final byte[] audioData, final float position) {
        super(sender);
        this.audioData = audioData;
        this.position = position;
    }

    @Override
    public byte[] getAudioData() {
        return audioData;
    }
}