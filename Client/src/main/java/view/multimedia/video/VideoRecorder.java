package view.multimedia.video;

public abstract class VideoRecorder implements Runnable {

    public static final int MAX_FPS = 15;
    public static final int COLOR_BYTES = 3;
    protected static final int MAX_DATA_SIZE = 42768;

    protected boolean isRunning;
    protected boolean isRecording;

    /**
     * Startet einen Thread zum Aufnehmen von Videodaten, sofern nicht bereits einer läuft.
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
     * Startet das Aufnehmen und Senden von Videoframes.
     */
    public synchronized void startRecording() {
        isRecording = true;
        notifyAll();
    }

    /**
     * Stoppt das Aufnehmen und Senden von Videoframes.
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

    protected void send() {

    }
}
