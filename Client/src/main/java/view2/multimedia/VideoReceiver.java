package view2.multimedia;

import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view2.Chati;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Eine Klasse, welches Videoframes erhält und diese zur Anzeige zur Verfügung stellt.
 */
public class VideoReceiver {

    private final Queue<VideoFrame> frameDataBuffer;

    /**
     * Erzeugt eine neue Instanz des VideoReceiver.
     */
    public VideoReceiver() {
        this.frameDataBuffer = new LinkedBlockingQueue<>();
    }

    /**
     * Hinterlegt ein erhaltenes VideoFrame.
     * @param userId ID des Benutzers, dessen Frame erhalten wurde.
     * @param timestamp Zeitstempel des Frames.
     * @param frameData Daten des Frames.
     * @throws UserNotFoundException falls kein Benutzer mit der ID gefunden wurde.
     */
    public void receiveVideoFrame(@NotNull final UUID userId, @NotNull final LocalDateTime timestamp,
                                  final byte[] frameData) throws UserNotFoundException {
        IUserView sender;
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null && internUser.getUserId().equals(userId)) {
            sender = internUser;
        } else {
            sender = Chati.CHATI.getUserManager().getExternUserView(userId);
        }
        frameDataBuffer.add(new VideoFrame(sender, timestamp, frameData));
    }

    /**
     * Gibt zurück, ob Frames vorhanden sind.
     * @return true, wenn Frames vorhanden sind, sonst false.
     */
    public boolean hasFrame() {
        return !frameDataBuffer.isEmpty();
    }

    /**
     * Gibt den nächsten anzuzeigenden Frame zurück.
     * @return Nächster anzuzeigender Frame.
     */
    public @Nullable VideoFrame getNextFrame() {
        return frameDataBuffer.poll();
    }

    /**
     * Eine Klasse, welche einen Videoframe repräsentiert.
     */
    public static class VideoFrame {

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
}
