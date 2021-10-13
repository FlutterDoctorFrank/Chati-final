package view.multimedia.video;

import controller.network.ServerSender;
import utils.VideoUtils;
import view.Chati;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

/**
 * Eine Klasse, durch welche das Aufnehmen und Senden von Bildschirmaufnahmen realisiert wird.
 */
public class ScreenRecorder extends VideoRecorder {

    public static final int FRAME_WIDTH = 850;
    public static final int FRAME_HEIGHT = 480;
    private static final float COMPRESSION_QUALITIY = 0.25f;

    private Robot robot;

    @Override
    public void start() {
        if (robot != null) {
            return;
        }
        super.start();
    }

    @Override
    public void run() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            Chati.LOGGER.log(Level.WARNING, "Error during Window toolkit initialization.", e);
            isRunning = false;
        }
        Rectangle screenRectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        long now = System.currentTimeMillis();
        long timer = 0;
        long deltaTime;

        outer:
        while (isRunning && robot != null) {
            try {
                synchronized (this) {
                    while (!isRecording) {
                        if (!isRunning) {
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

            BufferedImage screenshot = robot.createScreenCapture(screenRectangle);
            if (screenshot != null) {
                BufferedImage scaledScreenshot = VideoUtils.scaleImage(screenshot, FRAME_WIDTH, FRAME_HEIGHT);
                Chati.CHATI.getMultimediaManager().receiveVideoFrame(scaledScreenshot, true);
                byte[] compressedData = VideoUtils.compress(scaledScreenshot, COMPRESSION_QUALITIY, MAX_DATA_SIZE);
                if (compressedData.length != 0 && compressedData.length <= MAX_DATA_SIZE) {
                    Chati.CHATI.send(ServerSender.SendAction.VIDEO, true, compressedData);
                }
            }
        }

        isRecording = false;
        isRunning = false;
        robot = null;
    }

    @Override
    public boolean isRunning() {
        return super.isRunning() && robot != null;
    }
}
