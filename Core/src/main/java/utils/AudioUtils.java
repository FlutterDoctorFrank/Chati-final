package utils;

import javax.sound.sampled.AudioFormat;

/**
 * Eine Klasse, welche Hilfsmethoden für Audioanwendungen zur Verfügung stellt.
 */
public class AudioUtils {

    public static final AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;
    public static final int SAMPLING_RATE = 44100;
    public static final int SAMPLE_SIZE_IN_BITS = 16;
    public static final int SAMPLE_SIZE_IN_BYTES = SAMPLE_SIZE_IN_BITS / 8;
    public static final int CHANNELS = 1;
    public static final boolean MONO = CHANNELS == 1;
    public static final int FRAME_RATE = 30;
    public static final int FRAME_SIZE = SAMPLE_SIZE_IN_BYTES * SAMPLING_RATE / FRAME_RATE;

    private AudioUtils() {
    }

    /**
     * Kopiert Daten aus einem Short-Array in einen Byte-Array doppelter Größe.
     * @param shorts Short-Array der zu kopierenden Daten.
     * @param bigEndian Endianität der Daten.
     * @return Byte-Array mit den kopierten Daten.
     */
    public static byte[] toByte(final short[] shorts, final boolean bigEndian) {
        byte[] bytes = new byte[shorts.length * 2];
        if (bigEndian) {
            for (int i = 0; i < shorts.length; i++) {
                bytes[2 * i] = (byte) (shorts[i] >> 8);
                bytes[2 * i + 1] = (byte) shorts[i];
            }
        } else {
            for (int i = 0; i < shorts.length; i++) {
                bytes[2 * i] = (byte) shorts[i];
                bytes[2 * i + 1] = (byte) (shorts[i] >> 8);
            }
        }
        return bytes;
    }

    /**
     * Kopiert Daten aus einem Byte-Array in einen Short-Array halber Größe.
     * @param bytes Byte-Array der zu kopierenden Daten.
     * @param bigEndian Endianität der Daten.
     * @return Short-Array mit den kopierten Daten.
     */
    public static short[] toShort(final byte[] bytes, final boolean bigEndian) {
        short[] shorts = new short[bytes.length / 2];
        if (bigEndian) {
            for (int i = 0; i < shorts.length; i++) {
                shorts[i] = (short) ((bytes[2 * i] << 8) | (bytes[2 * i + 1] & 0xff));
            }
        } else {
            for (int i = 0; i < shorts.length; i++) {
                shorts[i] = (short) ((bytes[2 * i] & 0xff) | (bytes[2 * i + 1] << 8));
            }
        }
        return shorts;
    }

    /**
     * Wandelt Stereodaten in Monodaten um.
     * @param stereoData Stereodaten.
     * @return Monodaten.
     */
    public static byte[] toMono(final byte[] stereoData) {
        byte[] monoData = new byte[stereoData.length / 2];
        for (int i = 0; i < monoData.length; i += 2) {
            monoData[i] = stereoData[2 * i];
            monoData[i + 1] = stereoData[2 * i + 1];
        }
        return monoData;
    }
}
