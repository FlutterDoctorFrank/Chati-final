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
    private static final int SAMPLE_RATE = 44100;

    /** Frequenz der gesendeten Pakete. */
    private static final int SEND_RATE = 30;

    /** Mono oder Stereo. */
    private static final boolean MONO = false;

    /** Größe der gesendeten Pakete. */
    private static final int PACKET_SIZE = 2 * SAMPLE_RATE / SEND_RATE;


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

    /** Menü-Option, um die abzuspielende Stelle eines Musikstücks auszuwählen. */
    private static final int MENU_OPTION_SET_POSITION = 8;

    private ByteBuffer musicStreamBuffer;

    private ContextMusic currentMusic;

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
    public void interact(@NotNull User user) throws IllegalInteractionException {
        throwIfUserNotAvailable(user);

        // Öffne das Menü beim Benutzer.
        user.setCurrentInteractable(this);
        user.setMovable(false);
        user.send(ClientSender.SendAction.OPEN_MENU, this);
    }

    @Override
    public void executeMenuOption(@NotNull final User user, final int menuOption,
                                  @NotNull final String[] args) throws IllegalInteractionException, IllegalMenuActionException {
        super.executeMenuOption(user, menuOption, args);

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
                currentMusic = music;
                isPaused = false;
                loadBuffer();
                synchronized (this) {
                    notifyAll();
                }
                break;
            case MENU_OPTION_PAUSE: // Pausiere das Abspielen des Musikstücks oder setze es fort, falls es pausiert ist.
                if (musicStreamBuffer == null || !isRunning) {
                    throw new IllegalMenuActionException("", "objects.music-player.pause-not-possible");
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
                    throw new IllegalMenuActionException("", "objects.music-player.stop-not-possible");
                }
                isPaused = true;
                musicStreamBuffer.position(0);
                break;
            case MENU_OPTION_PREVIOUS: // Beginne das momentane Musikstück von vorn, oder spiele das letzte ab, falls
                                        // das momentane am Anfang ist.
                if (musicStreamBuffer == null || !isRunning || currentMusic == null) {
                    throw new IllegalMenuActionException("", "objects.music-player.play-previous-not-possible");
                }
                // TODO Nur bei unter 5 Sekunden Abspielzeit des aktuellen Lieds zum vorherigen, sonst neustarten
                musicStreamBuffer.clear();
                currentMusic = ContextMusic.values()[(currentMusic.ordinal() - 1) % ContextMusic.values().length];
                loadBuffer();
                break;
            case MENU_OPTION_NEXT: // Spiele das nächste Musikstück ab.
                if (musicStreamBuffer == null || !isRunning || currentMusic == null) {
                    throw new IllegalMenuActionException("", "objects.music-player.play-next-not-possible");
                }
                musicStreamBuffer.clear();
                currentMusic = ContextMusic.values()[(currentMusic.ordinal() + 1) % ContextMusic.values().length];
                loadBuffer();
                break;
            case MENU_OPTION_LOOPING: // Ein abgespieltes Lied wird immer wiederholt.
                if (args.length < 1) {
                    throw new IllegalMenuActionException("", "object.arguments.to-few");
                }
                boolean isLooping = Boolean.parseBoolean(args[0]);
                if (this.isLooping && isLooping || !this.isLooping && !isLooping) {
                    throw new IllegalMenuActionException("", "objects.music-player.illegal.looping.argument");
                }
                this.isLooping = isLooping;
                break;
            case MENU_OPTION_RANDOM: // Nach Ablauf eines Musikstücks wird ein zufälliges anderes abgespielt, sofern
                                     // nicht Looping eingestellt ist.
                if (args.length < 1) {
                    throw new IllegalMenuActionException("", "object.arguments.to-few");
                }
                boolean isRandom = Boolean.parseBoolean(args[0]);
                if (this.isRandom && isRandom || !this.isRandom && !isRandom) {
                    throw new IllegalMenuActionException("", "objects.music-player.illegal.random.argument");
                }
                this.isRandom = isRandom;
                break;
            case MENU_OPTION_SET_POSITION: // Setze die momentane Position im abgespielten Musikstück.
                // TODO
                break;
            default:
                throw new IllegalInteractionException("No valid menu option", user);
        }
    }

    private byte[] toMono(byte[] stereoData) {
        byte[] monoData = new byte[stereoData.length / 2];
        for (int i = 0; i < monoData.length; i += 2) {
            monoData[i] = stereoData[2 * i];
            monoData[i + 1] = stereoData[2 * i + 1];
        }
        return monoData;
    }

    private void loadBuffer() throws IllegalMenuActionException {
        try {
            InputStream file = getClass().getClassLoader().getResourceAsStream(currentMusic.getPath());
            if (file == null) {
                throw new IllegalMenuActionException("", "objects.music-player.error.loading.music");
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            byte[] inputData = audioInputStream.readAllBytes();
            byte[] musicStreamData;
            if (MONO) {
                musicStreamData = toMono(inputData);
            } else {
                musicStreamData = inputData;
            }
            musicStreamBuffer = ByteBuffer.allocate(musicStreamData.length);
            musicStreamBuffer.put(musicStreamData);
            musicStreamBuffer.position(0);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalMenuActionException("", e, "objects.music-player.error.loading.music");
        }
    }

    private void start() {
        if (isRunning) {
            return;
        }
        this.isRunning = true;

        Thread streamingThread = new Thread(() -> {
            byte[] sendData = new byte[PACKET_SIZE];
            while (isRunning) {
                synchronized (this) {
                    while (isPaused || !musicStreamBuffer.hasRemaining()) {
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
                            currentMusic = ContextMusic.values()[new Random().nextInt(ContextMusic.values().length)];
                            try {
                                loadBuffer();
                            } catch (IllegalMenuActionException e) {
                                e.printStackTrace();
                            }
                        } else {
                            isPaused = true;
                        }
                    }
                }
                AudioMessage message = new AudioMessage(null, sendData);
                getParent().getUsers().values().forEach(receiver -> receiver.send(ClientSender.SendAction.AUDIO, message));

                try {
                    Thread.sleep(1000 / SEND_RATE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        streamingThread.setDaemon(true);
        streamingThread.start();
    }
}
