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

public class MusicStreamer extends Interactable implements Runnable {

    /** Sample-Rate der gestreamten Musik. */
    private static final int SAMPLING_RATE = 44100;

    /** Frequenz der gesendeten Pakete. */
    private static final int SEND_RATE = 30;

    /** Größe der gesendeten Pakete. */
    private static final int BLOCK_SIZE = 2 * SAMPLING_RATE / SEND_RATE;

    /** Mono oder Stereo. */
    private static final boolean MONO = true;

    /** Zeit in Sekunden, bis zu dem das aktuelle Musikstück durch die MENU_OPTION_PREVIOUS neugestartet wird. */
    private static final float RESTART_SECONDS = 5;

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

    /** Der ByteBuffer der abzuspielenden Musik. */
    private ByteBuffer musicStreamBuffer;

    /** Die Information, ob der Streamingthread gerade aktiv ist. */
    private boolean isRunning;

    /** Die Information, ob das Senden von Musikdaten gerade pausiert ist. */
    private boolean isPaused;

    /** Dient dazu, dem Streamingthread die Information zu vermitteln, dass sich das Musikstück geändert hat. */
    private volatile boolean songChanged;

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
                if (!isPaused) {
                    play();
                }
                break;
            case MENU_OPTION_PAUSE: // Pausiere das Abspielen des Musikstücks oder setze es fort, falls es pausiert ist.
                if (musicStreamBuffer == null || !isRunning) {
                    throw new IllegalMenuActionException("", "object.music-player.pause-not-possible");
                }
                if (isPaused) {
                    play();
                } else {
                    pause();
                }
                break;
            case MENU_OPTION_STOP: // Stoppe das Abspielen des Musikstücks.
                if (musicStreamBuffer == null || musicStreamBuffer.position() == 0 || !isRunning) {
                    throw new IllegalMenuActionException("", "object.music-player.stop-not-possible");
                }
                setMusic(null);
                break;
            case MENU_OPTION_PREVIOUS: // Beginne das momentane Musikstück von vorn, oder spiele das letzte ab, falls
                                        // das momentane am Anfang ist.
                if (musicStreamBuffer == null || !isRunning || getMusic() == null) {
                    throw new IllegalMenuActionException("", "object.music-player.previous-not-possible");
                }
                if (getCurrentPlaytime() < RESTART_SECONDS) {
                    setMusic(ContextMusic.values()[(getMusic().ordinal() - 1) % ContextMusic.values().length]);
                } else {
                    setMusic(getMusic());
                }
                loadBuffer();
                if (!isPaused) {
                    play();
                }
                break;
            case MENU_OPTION_NEXT: // Spiele das nächste Musikstück ab.
                if (musicStreamBuffer == null || !isRunning || getMusic() == null) {
                    throw new IllegalMenuActionException("", "object.music-player.next-not-possible");
                }
                setMusic(ContextMusic.values()[(getMusic().ordinal() + 1) % ContextMusic.values().length]);
                loadBuffer();
                if (!isPaused) {
                    play();
                }
                break;
            case MENU_OPTION_LOOPING: // Ein abgespieltes Lied wird immer wiederholt.
                parent.setLooping(!parent.isLooping());
                break;
            case MENU_OPTION_RANDOM: // Nach Ablauf eines Musikstücks wird ein zufälliges anderes abgespielt, sofern
                                     // nicht Looping eingestellt ist.
                parent.setRandom(!parent.isRandom());
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

        Thread streamingThread = new Thread(this);
        streamingThread.setDaemon(true);
        streamingThread.start();
    }

    @Override
    public void run() {
        byte[] sendData = new byte[BLOCK_SIZE];

        outer:
        while (isRunning) {
            try {
                synchronized (this) {
                    // Warte, solange die Musik pausiert ist oder keine Daten verfügbar sind.
                    while (isPaused || !musicStreamBuffer.hasRemaining()) {
                        if (!isRunning) {
                            break outer;
                        }
                        wait();
                    }
                }

                // Wenn sich die Musik geändert hat, warte eine gewisse Zeit um Empfängern die Möglichkeit zu geben
                // ihre Puffer zu entleeren.
                if (songChanged) {
                    songChanged = false;
                    Thread.sleep(250);
                }

                // Sende Daten des laufenden Musikstücks, solange diese vorhanden sind.
                if (musicStreamBuffer.remaining() > sendData.length) {
                    musicStreamBuffer.get(sendData);
                } else {
                    // Sende letzten Block des laufenden Musikstücks.
                    musicStreamBuffer.get(sendData, 0, musicStreamBuffer.remaining());
                    try {
                        if (!parent.isLooping()) {
                            if (parent.isRandom()) { // Starte zufälliges nächstes Lied, wenn Random und nicht Looping eingestellt ist.
                                setMusic(ContextMusic.values()[new Random().nextInt(ContextMusic.values().length)]);
                            } else { // Starte nächstes Lied in der Liste, wenn nicht Random und nicht Looping eingestellt ist.
                                if (getMusic() != null) {
                                    setMusic(ContextMusic.values()[(getMusic().ordinal() + 1) % ContextMusic.values().length]);
                                }
                            }
                        } else { // Gebe gleiches Lied nochmal wieder, wenn Looping eingestellt ist.
                            setMusic(getMusic());
                        }
                        loadBuffer();
                    } catch (IllegalMenuActionException e) {
                        e.printStackTrace();
                    }
                }

                // Ermittle die aktuelle Position im laufenden Musikstück und erzeuge die Nachricht.
                float position = (float) musicStreamBuffer.position() / musicStreamBuffer.capacity();
                if (getMusic() != null) {
                    AudioMessage message = new AudioMessage(sendData, getMusic(), position, getCurrentPlaytime());

                    // Sende das Paket mit Musikdaten.
                    getParent().getUsers().values().forEach(receiver -> receiver.send(ClientSender.SendAction.AUDIO, message));
                }

                // Warte für die ungefähre Abspielzeit eines Pakets. Die genaue Zeitspanne die gewartet werden muss, kann
                // individuell sein und muss ermittelt werden, da sich die Geschwindigkeit von Server und Client
                // unterscheiden können.
                Thread.sleep(955 / SEND_RATE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gibt die Position des aktuell abgespielten Lieds in Sekunden zurück.
     * @return Position des aktuell abgespielten Lieds in Sekunden.
     */
    public int getCurrentPlaytime() {
        if (getMusic() == null) {
            return 0;
        }
        return musicStreamBuffer.position() / (2 * SAMPLING_RATE);
    }

    @Override
    public void setMusic(@Nullable final ContextMusic music) {
        songChanged = true;
        musicStreamBuffer = ByteBuffer.allocate(0);
        parent.setMusic(music);
    }

    @Override
    public @Nullable ContextMusic getMusic() {
        return parent.getMusic();
    }

    /**
     * Sendet Musik.
     */
    private void play() {
        isPaused = false;
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * Unterbricht das Senden von Musik.
     */
    private void pause() {
        isPaused = true;
    }
}
