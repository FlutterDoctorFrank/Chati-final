package model.context.spatial.objects;

import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.communication.message.AudioMessage;
import model.context.spatial.Area;
import model.context.spatial.ContextMenu;
import model.context.spatial.ContextMusic;
import model.context.spatial.Expanse;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Set;

public class MusicStreamer extends Interactable {

    /** Sample-Rate der gestreamten Musik. */
    private static final int SAMPLING_RATE = 44100;

    /** Frequenz der gesendeten Pakete. */
    private static final int SEND_RATE = 30;

    /** Mono oder Stereo. */
    private static final boolean MONO = true;

    /** Größe der gesendeten Pakete. */
    private static final int BLOCK_SIZE = 2 * SAMPLING_RATE / SEND_RATE;

    /** Menü-Option zum Auswählen eines abzuspielenden Musikstücks. */
    private static final int MENU_OPTION_PLAY = 1;

    /** Menü-Option zum Pausieren oder Fortsetzen des gerade ausgewählten Musikstücks. */
    private static final int MENU_OPTION_PAUSE = 2;

    /** Menü-Option zum Stoppen des gerade ausgewählten Musikstücks. */
    private static final int MENU_OPTION_STOP = 3;

    /** Menü-Option zur Wiedergabe des nächsten Musikstücks in der Liste. */
    private static final int MENU_OPTION_PREVIOUS = 4;

    /** Menü-Option zur Wiedergabe des vorherigen Musikstücks in der Liste. */
    private static final int MENU_OPTION_NEXT = 5;

    /** Menü-Option zum Ein- und Ausschalten der wiederholten Wiedergabe eines ausgewählten Musikstücks. */
    private static final int MENU_OPTION_LOOPING = 6;

    /** Menü-Option zum Ein- und Ausschalten von zufällig abzuspielenden Musikstücken. */
    private static final int MENU_OPTION_RANDOM = 7;

    private ByteBuffer musicStreamBuffer;

    private boolean isRunning;

    private boolean isPaused;

    private boolean isLooping;

    private boolean isRandom;

    /**
     * Erzeugt eine neue Instanz des MusicStreamer.
     * @param objectName Name des Objekts.
     * @param parent Übergeordneter Kontext.
     * @param expanse Räumliche Ausdehnung des Kontexts.
     * @param communicationRegion Geltende Kommunikationsform.
     * @param communicationMedia Benutzbare Kommunikationsmedien.
     */
    public MusicStreamer(@NotNull final String objectName, @NotNull final Area parent,
                       @NotNull final CommunicationRegion communicationRegion,
                       @NotNull final Set<CommunicationMedium> communicationMedia, @NotNull final Expanse expanse) {
        super(objectName, parent, communicationRegion, communicationMedia, expanse, ContextMenu.MUSIC_STREAMER_MENU);
        this.musicStreamBuffer = null;
        this.isRunning = false;
        this.isPaused = true;
        this.isLooping = false;
        this.isRandom = false;
        start();
    }

    @Override
    public void interact(@NotNull final User user) throws IllegalInteractionException {
        throwIfUserNotAvailable(user);
        throwIfInteractNotAllowed(user);
        // Öffne das Menü beim Benutzer.
        user.setCurrentInteractable(this);
        user.setMovable(false);
        user.send(ClientSender.SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(@NotNull final User user, final int menuOption,
                                     @NotNull final String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        if (executeCloseOption(user, menuOption)) {
            return;
        }

        switch (menuOption) {
            case MENU_OPTION_PLAY: // Spiele ein Musikstück ab.
                if (args.length < 1) {
                    throw new IllegalMenuActionException("", "object.arguments.to-few");
                }

                ContextMusic music;
                try {
                    music = ContextMusic.valueOf(args[0].toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalMenuActionException("", e, "object.music-player.music-not-found", args[0]);
                }
                setMusic(music);
                loadBuffer();
                isPaused = false;
                synchronized (this) {
                    notifyAll();
                }
                break;
            case MENU_OPTION_PAUSE: // Pausiere das Abspielen des Musikstücks oder setze es fort, falls es pausiert ist.
                if (musicStreamBuffer == null || !isRunning) {
                    throw new IllegalMenuActionException("", "object.music-player.pause-not-possible");
                }
                if (isPaused) {
                    isPaused = false;
                    synchronized (this) {
                        notifyAll();
                    }
                } else {
                    isPaused = true;
                }
                break;
            case MENU_OPTION_STOP: // Stoppe das Abspielen des Musikstücks.
                if (musicStreamBuffer == null || musicStreamBuffer.position() == 0 || !isRunning) {
                    throw new IllegalMenuActionException("", "object.music-player.stop-not-possible");
                }
                isPaused = true;
                setMusic(null);
                break;
            case MENU_OPTION_PREVIOUS: // Beginne das momentane Musikstück von vorn, oder spiele das letzte ab, falls
                                        // das momentane am Anfang ist.
                if (musicStreamBuffer == null || !isRunning || getMusic() == null) {
                    throw new IllegalMenuActionException("", "object.music-player.previous-not-possible");
                }
                if (getCurrentPlaytime() < 5) {
                    musicStreamBuffer.position(0);
                } else {
                    setMusic(ContextMusic.values()[(getMusic().ordinal() - 1) % ContextMusic.values().length]);
                    loadBuffer();
                }
                break;
            case MENU_OPTION_NEXT: // Spiele das nächste Musikstück ab.
                if (musicStreamBuffer == null || !isRunning || getMusic() == null) {
                    throw new IllegalMenuActionException("", "object.music-player.next-not-possible");
                }
                setMusic(ContextMusic.values()[(getMusic().ordinal() + 1) % ContextMusic.values().length]);
                loadBuffer();
                break;
            case MENU_OPTION_LOOPING: // Ein abgespieltes Lied wird immer wiederholt.
                this.isLooping = !this.isLooping;
                break;
            case MENU_OPTION_RANDOM: // Nach Ablauf eines Musikstücks wird ein zufälliges anderes abgespielt, sofern
                                     // nicht Looping eingestellt ist.
                this.isRandom = !this.isRandom;
                break;
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }

    /**
     * Wandelt Stereodaten in Monodaten um.
     * @param stereoData Stereodaten.
     * @return Monodaten.
     */
    private byte[] toMono(byte[] stereoData) {
        byte[] monoData = new byte[stereoData.length / 2];
        for (int i = 0; i < monoData.length; i += 2) {
            monoData[i] = stereoData[2 * i];
            monoData[i + 1] = stereoData[2 * i + 1];
        }
        return monoData;
    }

    /**
     * Lädt den Buffer mit Daten des aktuell ausgewählten Musikstücks.
     * @throws IllegalMenuActionException falls das Laden der Daten fehlschlägt.
     */
    private void loadBuffer() throws IllegalMenuActionException {
        try {
            if (getMusic() == null) {
                throw new IllegalMenuActionException("", "object.music-player.failed-loading");
            }
            InputStream file = getClass().getClassLoader().getResourceAsStream(getMusic().getPath());
            if (file == null) {
                throw new IllegalMenuActionException("", "object.music-player.failed-loading");
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            byte[] inputData = audioInputStream.readAllBytes();
            byte[] musicStreamData = MONO ? toMono(inputData) : inputData;
            musicStreamBuffer = ByteBuffer.allocate(musicStreamData.length);
            musicStreamBuffer.put(musicStreamData);
            musicStreamBuffer.position(0);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalMenuActionException("", e, "object.music-player.failed-loading");
        }
    }

    /**
     * Startet einen Thread zum Senden von Musikstreamingdaten.
     */
    private void start() {
        if (isRunning) {
            return;
        }
        this.isRunning = true;

        Thread streamingThread = new Thread(() -> {
            byte[] sendData = new byte[BLOCK_SIZE];

            outer:
            while (isRunning) {
                synchronized (this) {
                    // Warte, solange die Musik pausiert ist oder keine Daten verfügbar sind.
                    while (isPaused || !musicStreamBuffer.hasRemaining()) {
                        if (!isRunning) {
                            break outer;
                        }
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (musicStreamBuffer.remaining() > sendData.length) {
                    musicStreamBuffer.get(sendData);
                } else {
                    musicStreamBuffer.get(sendData, 0, musicStreamBuffer.remaining());
                    if (!isLooping) {
                        if (isRandom) {
                            setMusic(ContextMusic.values()[new Random().nextInt(ContextMusic.values().length)]);
                            try {
                                loadBuffer();
                            } catch (IllegalMenuActionException e) {
                                e.printStackTrace();
                            }
                        } else {
                            isPaused = true;
                        }
                    } else {
                        if (getMusic() != null) {
                            setMusic(ContextMusic.values()[(getMusic().ordinal() + 1) % ContextMusic.values().length]);
                            try {
                                loadBuffer();
                            } catch (IllegalMenuActionException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                AudioMessage message = new AudioMessage(null, sendData);
                getParent().getUsers().values().forEach(receiver -> receiver.send(ClientSender.SendAction.AUDIO, message));

                try {
                    Thread.sleep(955 / SEND_RATE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        streamingThread.setDaemon(true);
        streamingThread.start();
    }

    /**
     * Gibt die Position des aktuell abgespielten Lieds in Sekunden zurück.
     * @return Position des aktuell abgespielten Lieds in Sekunden.
     */
    public int getCurrentPlaytime() {
        if (getMusic() == null) {
            return 0;
        }
        return musicStreamBuffer.position() * SAMPLING_RATE;
    }

    @Override
    public void setMusic(@Nullable final ContextMusic music) {
        musicStreamBuffer.clear();
        parent.setMusic(music);
    }

    @Override
    public @Nullable ContextMusic getMusic() {
        return parent.getMusic();
    }
}
