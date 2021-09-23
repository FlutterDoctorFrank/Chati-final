package view2.audio;

import model.user.IUserView;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Eine Klasse, welche einen Sender von abzuspielenden Sprachdaten repräsentiert.
 */
public class VoiceChatUser extends AudioProducer {

    private final IUserView sender;

    /**
     * Erzeugt eine neue Instanz des VoiceChatUser.
     * @param sender Sendender Benutzer.
     * @param timestamp Zeitstempel der Daten.
     * @param voiceDataBlock Abzuspielende Daten.
     */
    public VoiceChatUser(@NotNull final IUserView sender, @NotNull final LocalDateTime timestamp, final short[] voiceDataBlock) {
        this.sender = sender;
        addAudioDataBlock(timestamp, voiceDataBlock);
    }

    @Override
    public void addAudioDataBlock(@NotNull final LocalDateTime timestamp, final short[] voiceDataBlock) {
        for (short sample : voiceDataBlock) {
            audioDataQueue.add(sample);
        }
        super.addAudioDataBlock(timestamp, voiceDataBlock);
    }

    /**
     * Gibt den sendenden Benutzer zurück.
     * @return Sendender Benutzer.
     */
    public @NotNull IUserView getSender() {
        return sender;
    }
}
