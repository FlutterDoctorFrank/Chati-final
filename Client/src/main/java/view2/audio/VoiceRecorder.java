package view2.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioRecorder;
import controller.network.ServerSender;
import view2.Chati;

public class VoiceRecorder {

    private static final short SEND_GATE = 1024;
    private static final long STOP_SENDING_DELAY = 250;

    private final AudioRecorder recorder;

    private boolean isRunning;

    private boolean isRecording;

    public VoiceRecorder() {
        this.recorder = Gdx.audio.newAudioRecorder(AudioManager.SAMPLE_RATE, AudioManager.MONO);
    }

    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;

        Thread recordAndSendThread = new Thread(() -> {
            long timestamp = System.currentTimeMillis();
            short[] recordedData = new short[AudioManager.BLOCK_SIZE];

            while (isRunning) {
                synchronized (this) {
                    // Warte solange nicht gesendet werden soll.
                    while (!isRecording) {
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
                    if (Math.abs(sample) >= SEND_GATE) {
                        timestamp = System.currentTimeMillis();
                        break;
                    }
                }
                if (System.currentTimeMillis() - timestamp < STOP_SENDING_DELAY) {
                    Chati.CHATI.send(ServerSender.SendAction.VOICE, AudioManager.toByte(recordedData, true));
                }
            }
        });
        recordAndSendThread.setDaemon(true);
        recordAndSendThread.start();
    }

    public void stop() {
        isRunning = false;
    }

    public synchronized void startRecording() {
        isRecording = true;
        notifyAll();
    }

    public void stopRecording() {
        isRecording = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isRecording() {
        return isRecording;
    }
}
