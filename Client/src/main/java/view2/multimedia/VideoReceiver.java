package view2.multimedia;

import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import model.user.IUserView;
import org.jetbrains.annotations.NotNull;
import view2.Chati;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Eine Klasse, welches Videoframes erhält und diese zur Anzeige zur Verfügung stellt.
 */
public class VideoReceiver {

    private final Queue<VideoFrame> frameDataBuffer;
    private final Map<IUserView, byte[]> frameParts;

    /**
     * Erzeugt eine neue Instanz des VideoReceiver.
     */
    public VideoReceiver() {
        this.frameDataBuffer = new LinkedBlockingQueue<>();
        this.frameParts = new HashMap<>();
    }

    /**
     * Hinterlegt ein erhaltenes VideoFrame.
     * @param userId ID des Benutzers, dessen Frame erhalten wurde.
     * @param timestamp Zeitstempel des Frames.
     * @param frameData Daten des Frames.
     * @throws UserNotFoundException falls kein Benutzer mit der ID gefunden wurde.
     */
    public void receiveVideoFrame(@NotNull final UUID userId, @NotNull final LocalDateTime timestamp,
                                  final byte[] frameData, final int number) throws UserNotFoundException {
        IUserView sender;
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser != null && internUser.getUserId().equals(userId)) {
            sender = internUser;
        } else {
            sender = Chati.CHATI.getUserManager().getExternUserView(userId);
        }

        if (!frameParts.containsKey(sender)) {
            frameParts.put(sender, new byte[VideoRecorder.PARTS * frameData.length]);
        }

        for (int i = 0; i < frameData.length; i++) {
            frameParts.get(sender)[number * frameData.length + i] = frameData[i];
        }

        if (number == VideoRecorder.PARTS - 1) {
            frameDataBuffer.add(new VideoFrame(sender, timestamp, frameParts.get(sender)));
        }
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
    public VideoFrame getNextFrame() {
        return frameDataBuffer.poll();
    }
}
