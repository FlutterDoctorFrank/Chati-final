package model.communication.message;

import model.communication.CommunicationMedium;

/**
 * Eine Enumeration, welche den durch ein vom Benutzer am Anfang der Nachricht eingegebenes Muster definierten
 * Nachrichtentyp einer Textnachricht repräsentiert.
 */
public enum MessageType {

    /**
     * Normale Nachricht gemäß der im räumlichen Kontext des Senders geltenden Kommunikationsform und
     * {@link CommunicationMedium Kommunikationsmedium}.
     */
    STANDARD,

    /**
     * Kann durch die Eingabe eines Chatbefehls am Anfang der Nachricht verwendet werden.
     * Flüsternachricht über eine Berechtigung oder gemäß der im räumlichen Kontext des Senders geltenden
     * Kommunikationsform und {@link CommunicationMedium Kommunikationsmedium}.
     */
    WHISPER,

    /**
     * Kann durch die Eingabe eines Chatbefehls am Anfang der Nachricht verwendet werden. Nachricht im innersten Bereich
     * des Senders.
     */
    AREA,

    /**
     * Kann durch die Eingabe eines Chatbefehls am Anfang der Nachricht verwendet werden. Nachricht im gesamten Raum
     * des Senders.
     */
    ROOM,

    /**
     * Kann durch die Eingabe eines Chatbefehls am Anfang der Nachricht verwendet werden. Nachricht in der gesamten Welt
     * des Senders.
     */
    WORLD,

    /**
     * Kann durch die Eingabe eines Chatbefehls am Anfang der Nachricht verwendet werden. Nachricht an alle Benutzer,
     * die sich in einer beliebigen Welt befinden.
     */
    GLOBAL,

    /**
     * Nachricht, welche nicht von einem Benutzer gesendet werden kann, sondern in bestimmten Situationen von der
     * Anwendung generiert wird und für informative Zwecke verwendet wird.
     */
    INFO
}
