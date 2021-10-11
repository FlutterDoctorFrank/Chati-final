package view.userInterface;

/**
 * Ein Interface, welches Methoden für Fensters in der Anwendung bereitstellt.
 */
public interface IChatiWindow {

    /**
     * Veranlasst das Öffnen des Fensters.
     */
    void open();

    /**
     * Veranlasst das Schließen des Fensters.
     */
    void close();

    /**
     * Fokussiert dieses Fenster.
     */
    void focus();
}
