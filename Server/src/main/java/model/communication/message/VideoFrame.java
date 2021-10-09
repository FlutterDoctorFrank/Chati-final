package model.communication.message;

import model.user.User;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Klasse, welche ein Videoframe repräsentiert.
 */
public class VideoFrame extends Message implements IVideoFrame {

    /** Daten des Frames. */
    private final byte[] frameData;

    /** Nummer des Frames. */
    private final int number;

    /**
     * Erzeugt eine neue Instanz eines Videoframes.
     * @param sender Sender des Frames.
     * @param frameData Daten des Frames.
     * @param number Nummer des Frames.
     */
    public VideoFrame(@NotNull final User sender, final byte[] frameData, final int number) {
        super(sender);
        this.frameData = frameData;
        this.number = number;
    }

    @Override
    public byte[] getFrameData() {
        return frameData;
    }

    @Override
    public int getNumber() {
        return number;
    }
}
