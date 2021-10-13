package view.multimedia.video;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import utils.VideoUtils;
import controller.network.ServerSender;
import view.Chati;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

/**
 * Eine Klasse, durch welche das Aufnehmen und Senden von Kameradaten realisiert wird.
 */
public class CameraRecorder extends VideoRecorder {

    public static final int FRAME_WIDTH = 640;
    public static final int FRAME_HEIGHT = 480;
    private static final float COMPRESSION_QUALITIY = 0.75f;

    private Webcam webcam;

    @Override
    public void start() {
        if (webcam != null && webcam.isOpen()) {
            return;
        }
        super.start();
    }

    @Override
    public void run() {
        try {
            webcam = Webcam.getDefault();
            webcam.setViewSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
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

        long now = System.currentTimeMillis();
        long timer = 0;
        long deltaTime;

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
                Chati.CHATI.getMultimediaManager().receiveVideoFrame(webcamImage, false);
                byte[] compressedData = VideoUtils.compress(webcamImage, COMPRESSION_QUALITIY, MAX_DATA_SIZE);
                if (compressedData.length != 0 && compressedData.length <= MAX_DATA_SIZE) {
                    Chati.CHATI.send(ServerSender.SendAction.VIDEO, false, compressedData);
                }
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

    @Override
    public boolean isRunning() {
        return super.isRunning() && webcam != null && webcam.isOpen();
    }

    /**
     * Gibt zurück, ob die Anwendung eine Kamera verwendet.
     * @return true, wenn eine Kamera verwendet wird, sonst false.
     */
    public boolean hasCamera() {
        return webcam != null && webcam.isOpen();
    }
}
