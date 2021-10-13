package view.multimedia.video;

import utils.VideoUtils;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.Chati;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Eine Klasse, welches Videoframes erhält und diese zur Anzeige zur Verfügung stellt.
 */
public class VideoReceiver {

    private final Queue<VideoFrame> videoFrameBuffer;

    /**
     * Erzeugt eine neue Instanz des VideoReceiver.
     */
    public VideoReceiver() {
        this.videoFrameBuffer = new LinkedBlockingQueue<>();
    }

    /**
     * Hinterlegt ein erhaltenes VideoFrame von einem externen Benutzer.
     * @param userId ID des Benutzers, dessen Frame erhalten wurde.
     * @param timestamp Zeitstempel des Frames.
     * @param screen true, wenn dieser Frame Teil einer Bildschirmaufnahme ist.
     * @param frameData Daten des Frames.
     * @throws UserNotFoundException falls kein Benutzer mit der ID gefunden wurde.
     */
    public void receiveVideoFrame(@NotNull final UUID userId, @NotNull final LocalDateTime timestamp,
                                  final boolean screen, final byte[] frameData) throws UserNotFoundException {
        IUserView sender = Chati.CHATI.getUserManager().getExternUserView(userId);
        BufferedImage frame = VideoUtils.read(frameData);
        if (frame != null) {
            byte[] decompressedData = ((DataBufferByte) frame.getRaster().getDataBuffer()).getData();
            videoFrameBuffer.add(new VideoFrame(sender, timestamp, screen, VideoUtils.toRGB(decompressedData)));
        }
    }

    /**
     * Hinterlegt ein erhaltenes VideoFrame vom intern angemeldeten Benutzer.
     * @param frame Erhaltenes Videoframe.
     * @param screen true, wenn dieser Frame Teil einer Bildschirmaufnahme ist.
     */
    public void receiveVideoFrame(@NotNull final BufferedImage frame, final boolean screen) {
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null) {
            BufferedImage preparedFrame = VideoUtils.scaleImage(frame, frame.getWidth(), frame.getHeight());
            if (!screen) {
                preparedFrame = VideoUtils.flipImageX(preparedFrame);
            }
            byte[] frameData = ((DataBufferByte) preparedFrame.getRaster().getDataBuffer()).getData();
            videoFrameBuffer.add(new VideoFrame(internUser, LocalDateTime.now(), screen, VideoUtils.toRGB(frameData)));
        }
    }

    /**
     * Gibt zurück, ob Frames von Kameraaufnahmen vorhanden sind.
     * @return true, wenn Frames vorhanden sind, sonst false.
     */
    public boolean hasFrame() {
        return !videoFrameBuffer.isEmpty();
    }

    /**
     * Gibt den nächsten anzuzeigenden Frame einer Kameraaufnahme zurück.
     * @return Nächster anzuzeigender Frame.
     */
    public @Nullable VideoFrame getNextFrame() {
        return videoFrameBuffer.poll();
    }

    /**
     * Eine Klasse, welche einen Videoframe repräsentiert.
     */
    public static class VideoFrame {

        private final IUserView sender;
        private final LocalDateTime timestamp;
        private final boolean screenshot;
        private final byte[] frameData;

        /**
         * Erzeugt eine neue Instanz des VideoFrame.
         * @param sender Sender des Frames.
         * @param timestamp Zeitstempel des Frames.
         * @param frameData Daten des Frames.
         */
        public VideoFrame(@NotNull final IUserView sender, @NotNull final LocalDateTime timestamp,
                          final boolean screenshot, final byte[] frameData) {
            this.sender = sender;
            this.timestamp = timestamp;
            this.screenshot = screenshot;
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
         * Gibt zurück, ob dieser Frame Teil einer Bildschirm- oder Kameraaufnahme ist.
         * @return true, wenn dieser Frame Teil einer Bildschirmaufnahme ist, sonst false.
         */
        public boolean isScreenshot() {
            return screenshot;
        }

        /**
         * Gibt die Daten des Frames zurück.
         * @return Daten des Frames.
         */
        public byte[] getFrameData() {
            return frameData;
        }
    }
}
