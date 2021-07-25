package model.user;

/**
 * Eine Enumeration, welche den Online-Status eines Benutzers repräsentiert.
 */
public enum Status {

    /**
     * Status eines eingeloggten Benutzers.
     */
    ONLINE,

    /**
     * Status eines Benutzers, der für eine festgelegte Zeit keine Aktion durchgeführt hat.
     */
    AWAY,

    /**
     * Status eines nicht eingeloggten Benutzers.
     */
    OFFLINE
}
