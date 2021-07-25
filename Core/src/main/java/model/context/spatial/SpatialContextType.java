package model.context.spatial;

/**
 * Eine Enumeration, welche den Typ eines räumlichen Kontextes festlegt.
 */
public enum SpatialContextType {

    /**
     * Ein räumlicher Kontext dieses Typs repräsentiert eine Welt, hat eine Karte des öffentlichen Raums dieser Welt
     * und kann weitere private Räume haben.
     * Mit einer Welt kann nicht interagiert werden.
     */
    WORLD,

    /**
     * Ein räumlicher Kontext dieses Typs repräsentiert einen privaten Raum, hat eine Karte, ein Passwort zum Betreten
     * und kann keine weiteren privaten Räume haben.
     * Mit einem privaten Raum kann nicht interagiert werden.
     */
    ROOM,

    /**
     * Ein räumlicher Kontext dieses Typs repräsentiert einen Bereich, hat keine Karte und kann keine weiteren privaten
     * Räume haben.
     * Mit einem Bereich kann interagiert werden, muss aber nicht.
     */
    AREA,

    /**
     * Ein räumlicher Kontext dieses Typs repräsentiert ein Objekt, hat keine Karte und kann keine weiteren privaten
     * Räume haben.
     * Mit einem Objekt kann interagiert werden.
     */
    OBJECT
}
