package view.multimedia.audio;

import model.user.IUserView;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Eine Klasse, welche einen Sender von abzuspielenden Sprachdaten repräsentiert.
 */
public class VoiceChatUser extends AudioProducer {

    /*
     * Beginne erst mit dem Abspielen von AudioDaten, wenn mindestens Daten für eine Abspieldauer von STARTING_DELAY in
     * Sekunden vorhanden sind. Dies führt zu einer kleinen Verzögerung, verhindert aber, dass sich bei einem
     * verspäteten Paket der Puffer direkt leert und das Abspielen der Daten unterbrochen wird.
     */
    private static final float VOICE_STARTING_DELAY = 0.25f;

    private final IUserView sender;

    /**
     * Erzeugt eine neue Instanz des VoiceChatUser.
     * @param sender Sendender Benutzer.
     * @param timestamp Zeitstempel der Daten.
     * @param voiceDataBlock Abzuspielende Daten.
     */
    public VoiceChatUser(@NotNull final IUserView sender, @NotNull final LocalDateTime timestamp, final short[] voiceDataBlock) {
        super(VOICE_STARTING_DELAY);
        this.sender = sender;
        addFrame(timestamp, voiceDataBlock);
    }

    @Override
    public void addFrame(@NotNull final LocalDateTime timestamp, final short[] voiceDataBlock) {
        for (short sample : voiceDataBlock) {
            receivedDataQueue.add(sample);
        }
        super.addFrame(timestamp, voiceDataBlock);
    }

    /**
     * Gibt zurück, ob der Benutzer gerade spricht.
     * @return true, wenn der Benutzer gerade spricht, sonst false.
     */
    public boolean isActive() {
        return hasData() && sender.canTalk();
    }

    /**
     * Gibt den sendenden Benutzer zurück.
     * @return Sendender Benutzer.
     */
    public @NotNull IUserView getSender() {
        return sender;
    }
}
