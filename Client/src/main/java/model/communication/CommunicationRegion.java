package model.communication;

/**
 * Eine Enumeration, welche das räumliche Ausmaß repräsentiert, über welches in einem räumlichen Kontext kommuniziert
 * werden kann.
 */
public enum CommunicationRegion {

    /**
     * Repräsentiert bereichs-basierte Kommunikation.
     * */
    AREA,

    /**
     * Repräsentiert das Benutzen der Kommunikationsform des übergeordneten Kontextes.
     * */
    PARENT,

    /**
     * Repräsentiert radius-basierte Kommunikation.
     * */
    RADIUS
}