package view2.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import controller.network.ServerSender;
import model.context.spatial.Expanse;
import model.context.spatial.Location;
import model.context.spatial.MapUtils;
import model.exception.UserNotFoundException;
import model.user.IUserView;
import view2.Chati;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class VoiceChat {

    private static final int SAMPLE_RATE = 22000;
    private static final int SEND_RATE = 30;
    private static final int PACKET_SIZE = SAMPLE_RATE / SEND_RATE;
    private static final int GATE = 1024;
    private static final long STOP_RECORD_DELAY = 250;

    private static VoiceChat voiceChat;

    private final ConcurrentHashMap<IUserView, short[]> mixDataBuffer;
    private final AudioRecorder recorder;
    private final AudioDevice player;

    private final Semaphore recordSemaphore;
    private final Semaphore playSemaphore;

    private boolean isRunning;
    private boolean recordAndSend;

    private VoiceChat() {
        this.recorder = Gdx.audio.newAudioRecorder(SAMPLE_RATE, true);
        this.player = Gdx.audio.newAudioDevice(SAMPLE_RATE, true);
        this.mixDataBuffer = new ConcurrentHashMap<>();
        this.recordSemaphore = new Semaphore(1);
        this.playSemaphore = new Semaphore(1);
    }

    public void start() {
        isRunning = true;
        recordAndSend();
        mixAndPlaybackAudio();
    }

    public void stop() {
        isRunning = false;
    }

    public void startSending() {
        recordAndSend = true;
        recordSemaphore.release();
    }

    public void stopSending() {
        recordAndSend = false;
    }

    public void receiveData(UUID userId, LocalDateTime timestamp, byte[] voiceData) throws UserNotFoundException {
        if (!isRunning) {
            return;
        }
        IUserView user = Chati.CHATI.getUserManager().getExternUserView(userId);
        mixDataBuffer.put(user, toShort(voiceData, false));
        playSemaphore.release();
    }

    /*
        Erzeuge und sende Audiopakete in einem Thread.
     */
    private void recordAndSend() {
        System.out.println("Ok Sende thread wird gestartet");
        Thread recordAndSendThread = new Thread(() -> {
            long timestamp = System.currentTimeMillis();
            short[] recordData = new short[SAMPLE_RATE / SEND_RATE];
            while (isRunning) {
                while (!recordAndSend) {
                    try {
                        recordSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                /*
                    Überprüfe ob Lautstärke eines Samples gewissen Wert überschreitet und setze Zeitstempel.
                    Nehme ab diesem Zeitpunkt mindestens für eine gewisse Dauer weiter auf, bevor die Aufnahme gestoppt
                    wird. Dadurch wird ein Dauersenden verhindert ohne am Ende "abgehackt" zu werden.
                */
                recorder.read(recordData, 0, recordData.length);
                for (short sample : recordData) {
                    if (Math.abs(sample) >= GATE) {
                        timestamp = System.currentTimeMillis();
                        break;
                    }
                }
                if (System.currentTimeMillis() - timestamp < STOP_RECORD_DELAY) {
                    Gdx.app.postRunnable(() -> {
                        Chati.CHATI.getServerSender().send(ServerSender.SendAction.VOICE, toByte(recordData, false));
                    });
                    System.out.println("Gesendet wird auch.");
                }
            }
        });
        recordAndSendThread.setDaemon(true);
        recordAndSendThread.start();
    }

    /*
        Mixe erhaltene Audiopakete und spiele Ergebnisse in einem Thread ab.
     */
    private void mixAndPlaybackAudio() {
        System.out.println("Abspielthread wird gestartet");
        Thread mixAndPlaybackThread = new Thread(() -> {
            short[] mixedData = new short[PACKET_SIZE];
            System.out.print("Hmmm");
            while (isRunning) {
                while (mixDataBuffer.isEmpty()) {
                    try {
                        playSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Mixe alle Audiodaten zusammen
                int[] temp = new int[PACKET_SIZE];
                for (int i = 0; i < PACKET_SIZE; i++) {
                    int j = i;
                    mixDataBuffer.forEach((id, data) -> temp[j] += data[j]);
                    mixedData[j] = (short) (temp[j] / mixDataBuffer.size());
                }
                // Lösche Bufferinhalte erhaltener Pakete nach dem Mixen
                mixDataBuffer.clear();
                System.out.println("Packets mixed");
                player.writeSamples(mixedData, 0, mixedData.length);
            }
        });
        mixAndPlaybackThread.setDaemon(true);
        mixAndPlaybackThread.start();
    }

    private byte[] toByte(short[] shorts, boolean littleEndian) {
        byte[] bytes = new byte[shorts.length * 2];
        if (littleEndian) {
            for (int i = 0; i < shorts.length; i++) {
                bytes[2 * i] = (byte) shorts[i];
                bytes[2 * i + 1] = (byte) (shorts[i] >> 8);
            }
        } else {
            for (int i = 0; i < shorts.length; i++) {
                bytes[2 * i] = (byte) (shorts[i] >> 8);
                bytes[2 * i + 1] = (byte) shorts[i];
            }
        }
        return bytes;
    }

    private short[] toShort(byte[] bytes, boolean littleEndian) {
        short[] shorts = new short[bytes.length / 2];
        if (littleEndian) {
            for (int i = 0; i < shorts.length; i++) {
                shorts[i] = (short) ((bytes[2 * i] & 0xff) | (bytes[2 * i + 1] << 8));
            }
        } else {
            for (int i = 0; i < shorts.length; i++) {
                shorts[i] = (short) ((bytes[2 * i] << 8) | (bytes[2 * i + 1] & 0xff));
            }
        }
        return shorts;
    }

    public static VoiceChat getInstance() {
        if (voiceChat == null) {
            voiceChat = new VoiceChat();
        }
        return voiceChat;
    }
}
