package model.communication.message;

/**
 * Ein Interface, welches dem Controller Methoden zur Verwaltung von Videoframes zur Verfügung stellt. Wird von
 * {@link VideoFrame} implementiert.
 */
public interface IVideoFrame extends IMessage {

    /**
     * Gibt die Daten des Videoframes zurück.
     * @return Daten des Videoframes.
     */
    byte[] getFrameData();

    /**
     * Gibt die Nummer des Videoframes zurück.
     * @return Nummer des Frames.
     */
    int getNumber();
}
