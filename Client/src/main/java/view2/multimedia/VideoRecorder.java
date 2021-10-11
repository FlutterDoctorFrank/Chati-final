package view2.multimedia;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import controller.network.ServerSender;
import model.exception.UserNotFoundException;
import model.user.IInternUserView;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import view2.Chati;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.logging.Level;

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

    private final ByteBuffer frameBuffer;
    private final byte[] frameData;
    private Webcam webcam;
    private boolean isRunning;
    private boolean isRecording;

    /**
     * Erzeugt eine neue Instanz des Videorecorder.
     */
    public VideoRecorder() {
        this.frameBuffer = BufferUtils.createByteBuffer(COLOR_BYTES * FRAME_WIDTH * FRAME_HEIGHT);
        this.frameData = new byte[COLOR_BYTES * FRAME_WIDTH * FRAME_HEIGHT];
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        long timer = 0;
        long deltaTime;

        try {
            webcam = Webcam.getDefault();
            webcam.setViewSize(new Dimension(CAMERA_WIDTH, CAMERA_HEIGHT));
        } catch (WebcamException e) {
            Chati.LOGGER.log(Level.WARNING, "Webcam not available.", e);
            isRunning = false;
        }

        try {
            webcam.open();
        } catch (WebcamException e) {
            // Webcam wird bereits verwendet.
            isRunning = false;
        }

        outer:
        while (isRunning && webcam != null && webcam.isOpen()) {
            try {
                synchronized (this) {
                    while (!isRecording) {
                        if (!isRunning || !webcam.isOpen()) {
                            break outer;
                        }
                        wait();
                    }
                }
                deltaTime = System.currentTimeMillis() - now;
                now = System.currentTimeMillis();
                timer += deltaTime;
                if (timer < 1000 / MAX_FPS) {
                    Thread.sleep(1000 / MAX_FPS - timer);
                    continue;
                }
                timer = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            BufferedImage webcamImage = webcam.getImage();
            if (webcamImage != null) {
                BufferedImage scaledImage = scaleImage(webcamImage, FRAME_WIDTH, FRAME_HEIGHT);
                frameBuffer.position(0);
                frameBuffer.put(((DataBufferByte) scaledImage.getRaster().getDataBuffer()).getData());
                frameBuffer.position(0);
                frameBuffer.get(frameData);
                Chati.CHATI.send(ServerSender.SendAction.VIDEO, toRGB(frameData));

                BufferedImage flippedImage = flipImageX(scaledImage);
                frameBuffer.position(0);
                frameBuffer.put(((DataBufferByte) flippedImage.getRaster().getDataBuffer()).getData());
                frameBuffer.position(0);
                frameBuffer.get(frameData);
                showFrame(toRGB(frameData));
            }
        }
        isRecording = false;
        isRunning = false;

        if (webcam != null) {
            try {
                webcam.close();
            } catch (WebcamException e) {
                Chati.LOGGER.log(Level.WARNING, "Could not close webcam.", e);
            }
        }
        webcam = null;
    }

    /**
     * Startet einen Thread zum Aufnehmen von Videodaten, sofern nicht bereits einer läuft.
     */
    public void start() {
        if (isRunning || webcam != null && webcam.isOpen()) {
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
        return isRunning && webcam != null && webcam.isOpen();
    }

    /**
     * Gibt zurück, ob gerade Sprachdaten aufgenommen werden.
     * @return true, wenn Sprachdaten aufgenommen werden, sonst false.
     */
    public boolean isRecording() {
        return isRecording;
    }

    /**
     * Skaliert ein gegebenes Bild.
     * @param bufferedImage Zu skalierendes Bild.
     * @param newWidth Neue Breite des Bildes.
     * @param newHeight Neue Höhe des Bildes.
     * @return Skaliertes Bild.
     */
    private @NotNull BufferedImage scaleImage(@NotNull final BufferedImage bufferedImage, final int newWidth, final int newHeight) {
        Image image = bufferedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = scaledImage.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return scaledImage;
    }

    /**
     * Flippt ein Bild entlang der X-Achse.
     * @param bufferedImage Zu flippendes Bild.
     * @return Geflipptes Bild.
     */
    private @NotNull BufferedImage flipImageX(@NotNull final BufferedImage bufferedImage) {
        AffineTransform flipX = AffineTransform.getScaleInstance(-1, 1);
        flipX.translate(-bufferedImage.getWidth(null), 0);
        return new AffineTransformOp(flipX, AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(bufferedImage, null);
    }

    /**
     * Wandelt ein Byte-Array aus BGR-Bilddaten in ein Byte-Array aus RGB-Bilddaten um.
     * @param bgr Byte-Array aus BGR-Bilddaten.
     * @return Byte-Array aus RGB-Bilddaten.
     */
    private byte[] toRGB(final byte[] bgr) {
        byte[] rgb = new byte[bgr.length];
        for (int i = 0; i < rgb.length; i += 3) {
            rgb[i] = bgr[i + 2];
            rgb[i + 1] = bgr[i + 1];
            rgb[i + 2] = bgr[i];
        }
        return rgb;
    }

    /**
     * Zeigt das aufgenommene Frame an.
     * @param frameData Datan des Frames.
     */
    private void showFrame(final byte[] frameData) {
        IInternUserView internUser = Chati.CHATI.getInternUser();
        if (internUser == null) {
            return;
        }
        try {
            Chati.CHATI.getMultimediaManager().receiveVideoFrame(internUser.getUserId(), LocalDateTime.now(), frameData);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }
}
