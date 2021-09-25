package model.communication.message;

import model.context.spatial.ContextMusic;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Eine Klasse, welche Audiodaten repräsentiert.
 */
public class AudioMessage extends Message implements IAudioMessage {

    /** Die Audiodaten der Sprachnachricht. */
    private final byte[] audioData;

    /** Die zugehörige Musik einer zusammenhängenden Audionachricht. */
    private final ContextMusic music;

    /** Die aktuelle Position in einer zusammenhängenden Audionachricht. */
    private final float position;

    /** Die aktuelle Sekunde in einer zusammenhängenden Audionachricht. */
    private final int seconds;

    /**
     * Erzeugt eine neue Instanz der Audionachricht. Wird für das Senden von Musikdaten verwendet.
     * @param musicData Die Musikdaten der Nachricht.
     * @param music Die Musik.
     * @param position Die aktuelle Position im Musikstück.
     * @param seconds Die aktuelle Sekunde im Musikstück.
     */
    public AudioMessage(final byte[] musicData, @NotNull final ContextMusic music, final float position, final int seconds) {
        this(null, musicData, music, position, seconds);
    }

    /**
     * Erzeugt eine neue Instanz der Audionachricht. Wird für Sprachnachrichten verwendet.
     * @param sender Der Sender dieser Nachricht.
     * @param voiceData die Sprachdaten der Nachricht.
     */
    public AudioMessage(@NotNull final User sender, final byte[] voiceData) {
        this(sender, voiceData, null, 0, 0);
    }

    /**
     * Erzeugt eine neue Instanz der Audionachricht.
     * @param sender Der Sender dieser Nachricht.
     * @param audioData Die Audiodaten der Nachricht.
     * @param music Die Musik einer zusammenhängenden Audionachricht.
     * @param position Aktuelle Position einer zusammenhängenden Audionachricht.
     * @param seconds Aktuelle Sekunde einer zusammenhängenden Audionachricht.
     */
    private AudioMessage(@Nullable final User sender, final byte[] audioData, @Nullable final ContextMusic music,
                         final float position, final int seconds) {
        super(sender);
        this.audioData = audioData;
        this.music = music;
        this.position = position;
        this.seconds = seconds;
    }

    @Override
    public byte[] getAudioData() {
        return audioData;
    }

    @Override
    public @Nullable ContextMusic getMusic() {
        return music;
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