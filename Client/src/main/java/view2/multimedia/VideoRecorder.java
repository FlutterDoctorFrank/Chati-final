package view2.multimedia;

import com.github.sarxos.webcam.Webcam;
import controller.network.ServerSender;
import org.lwjgl.BufferUtils;
import view2.Chati;

import java.awt.*;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

/**
 * Eine Klasse, durch welche das Aufnehmen und Senden von Webkameradaten reaisiert wird.
 */
public class VideoRecorder implements Runnable {

    public static final int FRAMES_PER_SECOND = 18;
    public static final int FRAME_WIDTH = 320;
    public static final int FRAME_HEIGHT = 240;
    public static final int COLOR_BYTES = 3;

    private final Webcam webcam;
    private final ByteBuffer frameBuffer;
    private final byte[] frameData;

    private boolean isRunning;
    private long deltaTime;

    /**
     * Erzeugt eine neue Instanz des Videorecorder.
     */
    public VideoRecorder() {
        this.webcam = Webcam.getDefault();
        this.webcam.setViewSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        this.frameBuffer = BufferUtils.createByteBuffer(COLOR_BYTES * FRAME_WIDTH * FRAME_HEIGHT);
        this.frameData = new byte[COLOR_BYTES * FRAME_WIDTH * FRAME_HEIGHT];
        this.deltaTime = 0;
    }

    @Override
    public void run() {
        outer:
        while (isRunning) {
            try {
                synchronized (this) {
                    while (!webcam.isOpen()) {
                        if (!isRunning) {
                            break outer;
                        }
                        wait();
                    }
                }
                long now = System.currentTimeMillis();
                if (now - deltaTime < 1000 / FRAMES_PER_SECOND) {
                    Thread.sleep(now - deltaTime);
                    continue;
                }
                deltaTime = now;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            frameBuffer.position(0);
            frameBuffer.put(((DataBufferByte) webcam.getImage().getRaster().getDataBuffer()).getData());
            frameBuffer.position(0);
            frameBuffer.get(frameData);

            Chati.CHATI.send(ServerSender.SendAction.VIDEO, frameData);
        }
        webcam.close();
    }

    /**
     * Startet einen Thread zum Aufnehmen von Videodaten, sofern nicht bereits einer läuft.
     */
    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;

        Thread captureThread = new Thread(this);
        captureThread.setDaemon(true);
        captureThread.start();
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
        webcam.open();
        notifyAll();
    }

    /**
     * Stoppt das Aufnehmen und Senden von Videoframes.
     */
    public void stopRecording() {
        webcam.close();
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
        return webcam.isOpen();
    }
}
