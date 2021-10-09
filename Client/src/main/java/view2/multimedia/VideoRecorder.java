package view2.multimedia;

import com.github.sarxos.webcam.Webcam;
import controller.network.ServerSender;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import org.lwjgl.BufferUtils;
import view2.Chati;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Eine Klasse, durch welche das Aufnehmen und Senden von Webkameradaten reaisiert wird.
 */
public class VideoRecorder implements Runnable {

    public static final int MAX_FPS = 24;
    public static final int FRAME_WIDTH = 320;
    public static final int FRAME_HEIGHT = 240;
    public static final int COLOR_BYTES = 3;
    public static final int PARTS = 4;

    private final Webcam webcam;
    private final ByteBuffer frameBuffer;
    private final byte[] frameData;
    private boolean isRunning;
    private boolean isRecording;

    /**
     * Erzeugt eine neue Instanz des Videorecorder.
     */
    public VideoRecorder() {
        this.webcam = Webcam.getDefault();
        this.webcam.setViewSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        this.frameBuffer = BufferUtils.createByteBuffer(COLOR_BYTES * FRAME_WIDTH * FRAME_HEIGHT);
        this.frameData = new byte[COLOR_BYTES * FRAME_WIDTH * FRAME_HEIGHT];
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        long deltaTime = now;

        outer:
        while (isRunning && webcam.isOpen()) {
            try {
                synchronized (this) {
                    while (!isRecording) {
                        if (!isRunning || !webcam.isOpen()) {
                            break outer;
                        }
                        wait();
                    }
                }
                now = System.currentTimeMillis();
                if (now - deltaTime < 1000 / MAX_FPS) {
                    Thread.sleep(now - deltaTime);
                    continue;
                }
                deltaTime = now;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            IInternUserView internUser = Chati.CHATI.getInternUser();
            BufferedImage webcamImage = webcam.getImage();
            if (internUser != null && webcamImage != null) {
                frameBuffer.position(0);
                frameBuffer.put(((DataBufferByte) webcamImage.getRaster().getDataBuffer()).getData());
                frameBuffer.position(0);
                frameBuffer.get(frameData);
                sendFrameData(frameData);
            }
        }
        isRecording = false;
        isRunning = false;
        if (webcam.isOpen()) {
            webcam.close();
        }
    }

    /**
     * Startet einen Thread zum Aufnehmen von Videodaten, sofern nicht bereits einer läuft.
     */
    public void start() {
        if (isRunning || webcam.isOpen()) {
            return;
        }
        webcam.open();
        isRunning = true;

        Thread captureThread = new Thread(this);
        captureThread.setDaemon(true);
        captureThread.start();
    }

    /**
     * Stoppt den gerade laufenden Aufnahme- und Sendethread.
     */
    public synchronized void stop() {
        webcam.close();
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
        return isRunning && webcam.isOpen();
    }

    /**
     * Gibt zurück, ob gerade Sprachdaten aufgenommen werden.
     * @return true, wenn Sprachdaten aufgenommen werden, sonst false.
     */
    public boolean isRecording() {
        return isRecording;
    }

    /**
     * Versendet die Daten des aktuellen Frames. Um das Netzwerk zu entlasten, werden die eigenen Frames nicht über den
     * Server zurück an den Sender gesendet, sondern hier direkt zum Abspielen im Client weitergeleitet.
     * @param frameData Zu sendende Daten.
     */
    private void sendFrameData(final byte[] frameData) {
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser == null) {
            return;
        }
        for (int i = 0; i < PARTS; i++) {
            byte[] sendData = Arrays.copyOfRange(frameData, i * frameData.length / PARTS, (i + 1) * frameData.length / PARTS);

            try {
                Chati.CHATI.getMultimediaManager().receiveVideoFrame(internUser.getUserId(), LocalDateTime.now(), sendData, i);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }

            Chati.CHATI.send(ServerSender.SendAction.VIDEO, sendData, i);
        }
    }
}
