package view2.multimedia;

import model.user.IUserView;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Eine Klasse, welche einen Videoframe repräsentiert.
 */
public class VideoFrame {

    private final IUserView sender;
    private final LocalDateTime timestamp;
    private final byte[] frameData;

    /**
     * Erzeugt eine neue Instanz des VideoFrame.
     * @param sender Sender des Frames.
     * @param timestamp Zeitstempel des Frames.
     * @param frameData Daten des Frames.
     */
    public VideoFrame(@NotNull final IUserView sender, @NotNull final LocalDateTime timestamp,
                      final byte[] frameData) {
        this.sender = sender;
        this.timestamp = timestamp;
        this.frameData = frameData;
    }

    /**
     * Gibt den Sender des Frames zurück.
     * @return Sender des Frames.
     */
    public @NotNull IUserView getSender() {
        return sender;
    }

    /**
     * Gibt den Zeitstempel des Frames zurück.
     * @return Zeitstempel des Frames.
     */
    public @NotNull LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gibt die Daten des Frames zurück.
     * @return Daten des Frames.
     */
    public byte[] getFrameData() {
        return frameData;
    }
}
