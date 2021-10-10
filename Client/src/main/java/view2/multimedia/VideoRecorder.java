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

/**
 * Eine Klasse, durch welche das Aufnehmen und Senden von Videodaten realisiert wird.
 */
public class VideoRecorder implements Runnable {

    public static final int MAX_FPS = 24;
    public static final int CAMERA_WIDTH = 320;
    public static final int CAMERA_HEIGHT = 240;
    public static final int FRAME_WIDTH = CAMERA_WIDTH / 2;
    public static final int FRAME_HEIGHT = CAMERA_HEIGHT / 2;
    public static final int COLOR_BYTES = 3;

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
        this.webcam.setViewSize(new Dimension(CAMERA_WIDTH, CAMERA_HEIGHT));
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

            BufferedImage webcamImage = webcam.getImage();
            if (webcamImage != null) {
                Image image = webcamImage.getScaledInstance(FRAME_WIDTH, FRAME_HEIGHT, Image.SCALE_SMOOTH);
                BufferedImage scaledImage = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D graphics = scaledImage.createGraphics();
                graphics.drawImage(image, 0, 0, null);
                graphics.dispose();

                frameBuffer.position(0);
                frameBuffer.put(((DataBufferByte) scaledImage.getRaster().getDataBuffer()).getData());
                frameBuffer.position(0);
                frameBuffer.get(frameData);
                sendFrameData(toRGB(frameData));
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
     * Wandelt ein Byte-Array aus BGR-Bilddaten in ein Byte-Array aus RGB-Bilddaten um.
     * @param bgr Byte-Array aus BGR-Bilddaten.
     * @return Byte-Array aus RGB-Bilddaten.
     */
    private byte[] toRGB(byte[] bgr) {
        byte[] rgb = new byte[bgr.length];
        for (int i = 0; i < rgb.length; i += 3) {
            rgb[i] = bgr[i + 2];
            rgb[i + 1] = bgr[i + 1];
            rgb[i + 2] = bgr[i];
        }
        return rgb;
    }

    /**
     * Versendet die Daten des aktuellen Frames. Um das Netzwerk zu entlasten, werden die Frames nicht zurück an den
     * Sender gesendet, sondern direkt zum Abspielen im Client weitergeleitet.
     * @param frameData Zu sendende Daten.
     */
    private void sendFrameData(final byte[] frameData) {
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser == null) {
            return;
        }
        try {
            Chati.CHATI.getMultimediaManager().receiveVideoFrame(internUser.getUserId(), LocalDateTime.now(), frameData);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        Chati.CHATI.send(ServerSender.SendAction.VIDEO, frameData);
    }
}
