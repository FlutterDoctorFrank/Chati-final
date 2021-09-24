package model.communication.message;

import model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Eine Klasse, welche Audiodaten repräsentiert.
 */
public class AudioMessage extends Message implements IAudioMessage {

    /** Die Audiodaten der Sprachnachricht. */
    private final byte[] audioData;

    /** Die aktuelle Position in einer zusammenhängenden Audionachricht. */
    private final float position;

    /** Die aktuelle Sekunde in einer zusammenhängenden Audionachricht. */
    private final int seconds;

    /**
     * Erzeugt eine neue Instanz der Audionachricht. Wird für das Senden von Musikdaten verwendet.
     * @param musicData Die Musikdaten der Nachricht.
     * @param position Die aktuelle Position im Musikstück.
     * @param seconds Die aktuelle Sekunde im Musikstück.
     */
    public AudioMessage(final byte[] musicData, final float position, final int seconds) {
        this(null, musicData, position, seconds);
    }

    /**
     * Erzeugt eine neue Instanz der Audionachricht. Wird für Sprachnachrichten verwendet.
     * @param sender Der Sender dieser Nachricht.
     * @param voiceData die Sprachdaten der Nachricht.
     */
    public AudioMessage(@NotNull final User sender, final byte[] voiceData) {
        this(sender, voiceData, 0, 0);
    }

    /**
     * Erzeugt eine neue Instanz der Audionachricht.
     * @param sender Der Sender dieser Nachricht.
     * @param audioData Die Audiodaten der Nachricht.
     * @param position Aktuelle Position einer zusammenhängenden Audionachricht.
     * @param seconds Aktuelle Sekunde einer zusammenhängenden Audionachricht.
     */
    private AudioMessage(@Nullable final User sender, final byte[] audioData, final float position, final int seconds) {
        super(sender);
        this.audioData = audioData;
        this.position = position;
        this.seconds = seconds;
    }

    @Override
    public byte[] getAudioData() {
        return audioData;
    }

    @Override
    public float getPosition() {
        return position;
    }

    @Override
    public int getSeconds() {
        return seconds;
    }
}