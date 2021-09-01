package view2.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.GdxRuntimeException;
import model.context.spatial.ContextMusic;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import view2.Chati;
import view2.Settings;
import view2.component.KeyAction;

import java.time.LocalDateTime;
import java.util.UUID;

public class AudioManager {

    private static final String CHAT_MESSAGE_SOUND_PATH = ""; // TODO
    private static final String NOTIFICATION_SOUND_PATH = ""; // TODO
    private static final String ROOM_ENTER_SOUND_PATH = ""; // TODO

    private VoiceChat voiceChat;
    private Sound chatMessageSound;
    private Sound notificationSound;
    private Sound roomEnterSound;
    private Music currentMusic;

    public AudioManager() {
        try {
            this.voiceChat = new VoiceChat();
        } catch (GdxRuntimeException e) {
            e.printStackTrace(); // Line is not available
        }
        //this.chatMessageSound = Gdx.audio.newSound(Gdx.files.internal(CHAT_MESSAGE_SOUND_PATH));
        //this.notificationSound = Gdx.audio.newSound(Gdx.files.internal(NOTIFICATION_SOUND_PATH));
        //this.roomEnterSound = Gdx.audio.newSound(Gdx.files.internal(ROOM_ENTER_SOUND_PATH));
        this.currentMusic = null;
    }

    public void update() {
        IInternUserView internUser = Chati.CHATI.getUserManager().getInternUserView();
        if (voiceChat != null) {
            if (Chati.CHATI.isUserInfoChanged() || Chati.CHATI.isWorldChanged()) {
                if (!voiceChat.isRunning() && internUser != null && internUser.isInCurrentWorld()) {
                    setVoiceVolume();
                    voiceChat.start();
                } else if (voiceChat.isRunning() && (internUser == null || !internUser.isInCurrentWorld())) {
                    voiceChat.stop();
                }
            }

            if (voiceChat.isRunning()) {
                if (Settings.isMicrophoneOn() && (!Settings.getPushToTalk() || KeyAction.PUSH_TO_TALK.isPressed())
                    && internUser != null && internUser.canTalk()) {
                    if (!voiceChat.isSending()) {
                        voiceChat.startSending();
                    }
                } else if (voiceChat.isSending()) {
                    voiceChat.stopSending();
                }
            }
        }

        if (Chati.CHATI.isMusicChanged()) {
            playMusic(Chati.CHATI.getUserManager().getInternUserView().getMusic());
        }
    }

    public void setVoiceVolume() {
        if (voiceChat != null) {
            float volume = (float) Math.sqrt(Settings.getTotalVolume() * Settings.getVoiceVolume());
            voiceChat.setVolume(volume);
        }
    }

    public void setMusicVolume() {
        if (currentMusic != null) {
            float volume = (float) Math.sqrt(Settings.getTotalVolume() * Settings.getMusicVolume());
            currentMusic.setVolume(volume);
        }
    }

    public void playVoiceData(UUID userId, LocalDateTime timestamp, byte[] voiceData) throws UserNotFoundException {
        if (voiceChat != null && voiceChat.isRunning()) {
            voiceChat.receiveData(userId, timestamp, voiceData);
        }
    }

    public void playChatMessageSound() {
        if (chatMessageSound != null) {
            float volume = (float) Math.sqrt(Settings.getTotalVolume() * Settings.getSoundVolume());
            chatMessageSound.play(volume);
        }
    }

    public void playNotificationSound() {
        if (notificationSound != null) {
            float volume = (float) Math.sqrt(Settings.getTotalVolume() * Settings.getSoundVolume());
            notificationSound.play(volume);
        }
    }

    public void playRoomEnterSound() {
        if (roomEnterSound != null) {
            float volume = (float) Math.sqrt(Settings.getTotalVolume() * Settings.getSoundVolume());
            roomEnterSound.play(volume);
        }
    }

    private void playMusic(ContextMusic contextMusic) {
        if (contextMusic != null) {
            currentMusic = Gdx.audio.newMusic(Gdx.files.internal(contextMusic.getPath()));
            currentMusic.setLooping(false);
            currentMusic.setOnCompletionListener(music ->
                    playMusic(ContextMusic.values()[((contextMusic.ordinal() + 1) % ContextMusic.values().length)]));
            setMusicVolume();
            currentMusic.play();
        } else {
            if (currentMusic != null) {
                currentMusic.dispose();
                currentMusic = null;
            }
        }
    }
}
