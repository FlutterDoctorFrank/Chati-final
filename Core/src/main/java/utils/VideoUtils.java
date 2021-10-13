package utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class VideoUtils {

    private VideoUtils() {
    }

    /**
     * Komprimiert ein gegebenes Bild mit einer gewünschten Qualität, bis eine maximale Größe unterschritten wurde.
     * @param image Zu komprimierendes Bild.
     * @param compressionQuality Komprimierungsqualität.
     * @param maxSize Maximale Größe.
     * @return Komprimiertes Bild.
     */
    public static byte[] compress(@NotNull final BufferedImage image, final float compressionQuality, final int maxSize) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] outData;
        float quality = compressionQuality;
        do {
            write(out, image, quality);
            outData = out.toByteArray();
            out.reset();
            quality -= 0.1f;
        } while (outData.length > maxSize && quality > 0);
        return outData;
    }

    /**
     * Schreibt ein komprimiertes Bild in einen ByteArrayOutputStream.
     * @param out Der OutputStream, auf den ein Bild geschrieben werden soll.
     * @param image Das zu schreibende Bild.
     * @param quality Komprimierungsqualität.
     */
    private static void write(@NotNull final ByteArrayOutputStream out, @NotNull final BufferedImage image,
                              final float quality) {
        try {
            Iterator<ImageWriter> iterator = ImageIO.getImageWritersBySuffix("jpeg");
            if (!iterator.hasNext()) {
                throw new IllegalStateException("No image-writers found.");
            }
            ImageWriter imageWriter = iterator.next();
            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(out);
            imageWriter.setOutput(imageOutputStream);
            ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
            if (quality >= 0 && quality <= 1) {
                imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                imageWriteParam.setCompressionQuality(quality);
            }
            imageWriter.write(null, new IIOImage(image, null, null), imageWriteParam);
            imageWriter.dispose();
            imageOutputStream.close();
        } catch (IOException e) {
            // Ignoriere
        }
    }

    /**
     * Ließt ein Bild aus einer Menge von Bytes.
     * @param bytes Zu lesende Bytes.
     * @return Gelesenes Bild.
     */
    public static @Nullable BufferedImage read(final byte[] bytes) {
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            // Ignoriere
        }
        return null;
    }

    /**
     * Skaliert ein gegebenes Bild.
     * @param bufferedImage Zu skalierendes Bild.
     * @param newWidth Neue Breite des Bildes.
     * @param newHeight Neue Höhe des Bildes.
     * @return Skaliertes Bild.
     */
    public static @NotNull BufferedImage scaleImage(@NotNull final BufferedImage bufferedImage,
                                                    final int newWidth, final int newHeight) {
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
    public static @NotNull BufferedImage flipImageX(@NotNull final BufferedImage bufferedImage) {
        AffineTransform flipX = AffineTransform.getScaleInstance(-1, 1);
        flipX.translate(-bufferedImage.getWidth(null), 0);
        return new AffineTransformOp(flipX, AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(bufferedImage, null);
    }

    /**
     * Wandelt ein Byte-Array aus BGR-Bilddaten in ein Byte-Array aus RGB-Bilddaten um.
     * @param bgrData Byte-Array aus BGR-Bilddaten.
     * @return Byte-Array aus RGB-Bilddaten.
     */
    public static byte[] toRGB(final byte[] bgrData) {
        byte[] rgb = new byte[bgrData.length];
        for (int i = 0; i < rgb.length; i += 3) {
            rgb[i] = bgrData[i + 2];
            rgb[i + 1] = bgrData[i + 1];
            rgb[i + 2] = bgrData[i];
        }
        return rgb;
    }
}
