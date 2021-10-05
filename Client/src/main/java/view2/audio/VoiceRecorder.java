package view2.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.utils.Disposable;
import controller.network.ServerSender;
import view2.Chati;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Eine Klasse, durch welche das Aufnehmen und Senden von Sprachdaten realisiert wird.
 */
public class VoiceRecorder implements Runnable, Disposable {

    private static final float LOOK_AHEAD = 0.25f; // in Sekunden
    private static final int MAX_BLOCKS = (int) (LOOK_AHEAD * AudioManager.SEND_RATE);
    private static final float STOP_SENDING_DELAY = 0.25f; // in Sekunden

    private final AudioRecorder recorder;
    private final Queue<short[]> sendDataQueue;
    private float sendGate;
    private boolean isRunning;
    private boolean isRecording;
    private boolean isSending;

    /**
     * Erzeugt eine neue Instanz des VoiceRecorder.
     */
    public VoiceRecorder() {
        this.recorder = Gdx.audio.newAudioRecorder(AudioManager.SAMPLING_RATE, AudioManager.MONO);
        this.sendDataQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        long timestamp = System.currentTimeMillis();

        outer:
        while (isRunning) {
            synchronized (this) {
                // Warte, solange nicht gesendet werden soll.
                while (!isRecording) {
                    isSending = false;
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

            short[] recordedData = new short[AudioManager.BLOCK_SIZE];
            recorder.read(recordedData, 0, recordedData.length);
            sendDataQueue.add(recordedData);
            if (sendDataQueue.size() > MAX_BLOCKS) {
                sendDataQueue.poll();
            }

            // Überprüfe ob Lautstärke eines Samples gewissen Wert überschreitet, sende dann alle Pakete im Sendepuffer
            // und setze Zeitstempel. Sende ab diesem Zeitpunkt mindestens für eine gewisse Dauer weiter, bevor das
            // Senden gestoppt wird. Dadurch wird ein Dauersenden verhindert, ohne am Ende "abgehackt" zu werden.
            for (short sample : recordedData) {
                if (Math.abs(sample) >= sendGate) {
                    timestamp = System.currentTimeMillis();
                    break;
                }
            }
            if (System.currentTimeMillis() - timestamp < 1000 * STOP_SENDING_DELAY) {
                short[] sendData = sendDataQueue.poll();
                while (sendData != null) {
                    Chati.CHATI.send(ServerSender.SendAction.VOICE, AudioManager.toByte(sendData, true));
                    sendData = sendDataQueue.poll();
                }
                isSending = true;
            } else {
                isSending = false;
            }
        }
        isSending = false;
    }

    /**
     * Startet einen Thread zum Aufnehmen von Sprachdaten, sofern nicht bereits einer läuft.
     */
    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;

        Thread recordAndSendThread = new Thread(this);
        recordAndSendThread.setDaemon(true);
        recordAndSendThread.start();
    }

    /**
     * Stoppt den gerade laufenden Aufnahme- und Sendethread.
     */
    public synchronized void stop() {
        isRunning = false;
        notifyAll();
    }

    /**
     * Startet das Aufnehmen und Senden von Sprachdaten.
     */
    public synchronized void startRecording() {
        isRecording = true;
        notifyAll();
    }

    /**
     * Stoppt das Aufnehmen und Senden von Sprachdaten.
     */
    public void stopRecording() {
        isRecording = false;
    }

    /**
     * Gibt zurück, ob gerade ein Aufnahme- und Sendethread aktiv ist.
     * @return true, wenn ein Thread aktiv ist, sonst false.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Gibt zurück, ob gerade Sprachdaten aufgenommen werden.
     * @return true, wenn Sprachdaten aufgenommen werden, sonst false.
     */
    public boolean isRecording() {
        return isRecording;
    }

    /**
     * Gibt zurück, ob gerade Sprachdaten gesendet werden.
     * @return true, wenn Sprachdaten gesendet werden, sonst false.
     */
    public boolean isSending() {
        return isSending;
    }

    @Override
    public void dispose() {
        recorder.dispose();
    }

    /**
     * Setzt das Gate zum Senden von Sprachdaten anhand der Mikrofonempfindlichkeit.
     * @param microphoneSensitivity Mikrofonempfindlichkeit zwischen 0 und 1.
     */
    public void setMicrophoneSensitivity(final float microphoneSensitivity) {
        if (microphoneSensitivity < 0 || microphoneSensitivity > 1) {
            return;
        }
        /*
         * Verwendung einer Exponentialfunktion. Wert für a wurde rechnerisch ermittelt, sodass:
         * - Bei maximaler Mikrophonempfindlichkeit alles aufgezeichnet wird. (SendGate = 0)
         * - Bei minimaler Mikrophonempfindlichkeit nichts aufgezeichnet wird. (SendGate = Short.MAX_VALUE)
         * - Bei mittlerer Mikrophonempfindlichkeit das SendGate auf 2048 gesetzt wird. Dieser Wert hat sich als
         *   geeignet herausgestellt, um normale Gesprächslautstärke aufzunehmen.
         */
        double a = 943656961d / 28671;
        this.sendGate = (float) (a * Math.pow((a - Short.MAX_VALUE) / a, microphoneSensitivity) + Short.MAX_VALUE - a);
    }
}
