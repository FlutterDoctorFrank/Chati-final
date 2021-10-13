package model.communication.message;

import model.user.User;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Klasse, welche ein Videoframe repräsentiert.
 */
public class VideoFrame extends Message implements IVideoFrame {

    /** Gibt an, ob dieses Frame von einer Bildschirmaufnahme oder einer Kameraaufnahme ist. */
    private final boolean screenshot;

    /** Daten des Frames. */
    private final byte[] frameData;

    /**
     * Erzeugt eine neue Instanz eines Videoframes.
     * @param sender Sender des Frames.
     * @param screenshot falls true, ist dieses Frame von einer Bildschirmaufnahme, sonst von einer Kameraaufnahme.
     * @param frameData Daten des Frames.
     */
    public VideoFrame(@NotNull final User sender, final boolean screenshot, final byte[] frameData) {
        super(sender);
        this.screenshot = screenshot;
        this.frameData = frameData;
    }

    @Override
    public boolean isScreenshot() {
        return screenshot;
    }

    @Override
    public byte[] getFrameData() {
        return frameData;
    }
}
