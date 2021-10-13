package model.communication.message;

/**
 * Ein Interface, welches dem Controller Methoden zur Verwaltung von Videoframes zur Verfügung stellt. Wird von
 * {@link VideoFrame} implementiert.
 */
public interface IVideoFrame extends IMessage {

    /**
     * Gibt an, ob dieses Frame von einer Bildschirmaufnahme oder einer Kameraaufnahme ist.
     * @return true, falls dieses Frame von einer Bildschirmaufnahme ist, sonst false.
     */
    boolean isScreenshot();

    /**
     * Gibt die Daten des Videoframes zurück.
     * @return Daten des Videoframes.
     */
    byte[] getFrameData();
}
