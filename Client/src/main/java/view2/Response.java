package view2;

/**
 * Ein Enum, welches die möglichen Antworten vom Server repräsentiert, die der Client nach einer durchgeführten Aktion
 * erwartet.
 */
public enum Response {
    /** Signalisiert, dass keine Antwort erwartet wird. */
    NONE,

    /** Signalisiert, dass die Antwort der Registrierung erwartet wird. */
    REGISTRATION,

    /** Signalisiert, dass die Antwort des Anmeldens erwartet wird. */
    LOGIN,

    /** Signalisiert, dass die Antwort für die Änderung des Passworts erwartet wird. */
    PASSWORD_CHANGE,

    /** Signalisiert, dass die Antwort für die Löschung des Kontos erwartet wird. */
    DELETE_ACCOUNT,

    /** Signalisiert, dass die Antwort für die Änderung des Avatars erwartet wird. */
    AVATAR_CHANGE,

    /** Signalisiert, dass die Antwort für die Erzeugung einer Welt erwartet wird. */
    CREATE_WORLD,

    /** Signalisiert, dass die Antwort für die Löschung einer Welt erwartet wird. */
    DELETE_WORLD,

    /** Signalisiert, dass die Antwort für das Betreten einer Welt erwartet wird. */
    JOIN_WORLD,

    /** Signalisiert, dass die Antwort für das Interagieren mit einem Interaktionsobjekt erwartet wird. */
    INTERACT,

    /** Signalisiert, dass die Antwort für das Schließen eines Menüs erwartet wird. */
    CLOSE_MENU,

    /** Signalisiert, dass die Antwort auf eine durchgeführte Menüaktion eines Interaktionsobjekts erwartet wird. */
    MENU_ACTION_RESPONSE
}
