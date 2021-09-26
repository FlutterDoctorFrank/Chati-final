package view2.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.utils.Disposable;
import controller.network.ServerSender;
import view2.Chati;

/**
 * Eine Klasse, durch welche das Aufnehmen und Senden von Sprachdaten realisiert wird.
 */
public class VoiceRecorder implements Runnable, Disposable {

    private static final float STOP_SENDING_DELAY = 0.25f; // in Sekunden

    private final AudioRecorder recorder;
    private float sendGate;
    private boolean isRunning;
    private boolean isRecording;

    /**
     * Erzeugt eine neue Instanz des VoiceRecorder.
     */
    public VoiceRecorder() {
        this.recorder = Gdx.audio.newAudioRecorder(AudioManager.SAMPLING_RATE, AudioManager.MONO);
    }

    @Override
    public void run() {
        short[] recordedData = new short[AudioManager.BLOCK_SIZE];
        long timestamp = System.currentTimeMillis();

        outer:
        while (isRunning) {
            synchronized (this) {
                // Warte, solange nicht gesendet werden soll.
                while (!isRecording) {
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

            // Überprüfe ob Lautstärke eines Samples gewissen Wert überschreitet, fange dann an zu senden und setze
            // Zeitstempel. Sende ab diesem Zeitpunkt mindestens für eine gewisse Dauer weiter, bevor das Senden
            // gestoppt wird. Dadurch wird ein Dauersenden verhindert, ohne am Ende "abgehackt" zu werden.
            recorder.read(recordedData, 0, recordedData.length);
            for (short sample : recordedData) {
                if (Math.abs(sample) >= sendGate) {
                    timestamp = System.currentTimeMillis();
                    break;
                }
            }
            if (System.currentTimeMillis() - timestamp < 1000 * STOP_SENDING_DELAY) {
                Chati.CHATI.send(ServerSender.SendAction.VOICE, AudioManager.toByte(recordedData, true));
            }
        }
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

    @Override
    public void dispose() {
        recorder.dispose();
    }

    /**
     * Setzt das Gate zum Senden von Sprachdaten anhand der Mikrofonempfindlichkeit.
     * @param microphoneSensitivity Mikrofonempfindlichkeit
     */
    public void setMicrophoneSensitivity(final float microphoneSensitivity) {
        // Verwendet Polynom zweiten Grades mit Werten an den Stellen 0, 0.5 und 1, die sich durch Erproben als geeignet
        // herausgestellt haben.
        sendGate = 4608 * microphoneSensitivity * microphoneSensitivity - 8448 * microphoneSensitivity + 4096;
    }
}
