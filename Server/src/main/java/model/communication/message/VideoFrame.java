package model.communication.message;

import model.user.User;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Klasse, welche ein Videoframe repräsentiert.
 */
public class VideoFrame extends Message implements IVideoFrame {

    /** Daten des Frames. */
    private final byte[] frameData;

    /**
     * Erzeugt eine neue Instanz eines Videoframes.
     * @param sender Sender des Frames.
     * @param frameData Daten des Frames.
     */
    public VideoFrame(@NotNull final User sender, final byte[] frameData) {
        super(sender);
        this.frameData = frameData;
    }

    @Override
    public byte[] getFrameData() {
        return frameData;
    }
}
