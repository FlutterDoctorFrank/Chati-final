package model.user;

/**
 * Eine Enumeration, die eine Durchführung einer administrativen Aktion auf einem Benutzer repräsentiert
 */
public enum AdministrativeAction {

    /**
     * Sendet einem Benutzer eine Freundschaftsanfrage, sofern dieser Benutzer nicht bereits in der Freundesliste vom
     * anfragenden Benutzer enthalten ist und der angefragte Benutzer den anfragenden Benutzer nicht ignoriert.
     * Ignoriert der anfragende Benutzer den angefragten, wird dieser durch das Senden der Freundschaftsanfrage
     * automatisch nicht mehr ignoriert.
     */
    INVITE_FRIEND,

    /**
     * Löscht einen Benutzer aus der Freundesliste. Verursacht auch das Löschen des ausführenden Benutzers in der
     * Freundesliste des zu löschenden Benutzers.
     */
    REMOVE_FRIEND,

    /**
     * Ignoriert einen Benutzer. Die Kommunikation zwischen einem ignorierenden und einem ignorierten Benutzer ist nicht
     * möglich. Das Ignorieren eines in der Freundesliste enthaltenen Benutzer zieht die automatische Löschung der
     * Freundschaft in den Freundeslisten beider Benutzer mit sich.
     */
    IGNORE_USER,

    /**
     * Ignoriert einen Benutzer nicht mehr.
     */
    UNIGNORE_USER,

    /**
     * Stellt einen Benutzer im innersten räumlichen Kontext stumm, in dem sich sowohl der ausführende, als auch der
     * stummzuschaltende Benutzer befinden, sofern der ausführende Benutzer die Berechtigung für diesen Kontext besitzt.
     * Ein stummgeschalteter Benutzer kann im entsprechenden Kontext nichtmehr aktiv kommunizieren, außer per
     * Flüsternachricht.
     */
    REPORT_USER,

    /**
     * Warnt einen Benutzer. Benutzer, mit der Berechtigung Benutzer sperren zu können, können Benutzer warnen. Ist
     * der Benutzer, der gewarnt wird gemeldet, so ist dieser nach der Warnung nicht mehr gemeldet. Benutzer, mit der
     * Berechtigung Benutzer zu sperren, können nur von Benutzern mit der Berechtigung Moderatoren zu sperren gewarnt
     * werden. Benutzer, mit der Berechtigung Moderatoren zu sperren, können nicht gewarnt werden.
     */
    WARN_USER,

    /**
     * Lädt einen Benutzer in einen privaten Raum ein, in dem sich der ausführende Benutzer befindet und für den er die
     * nötige Berechtigung besitzt.
     */
    MUTE_USER,

    /**
     * Entfernt einen Benutzer aus einem privaten Raum, in dem er sich befindet, und für den der ausführende Benutzer
     * die nötige Berechtigung besitzt.
     */
    UNMUTE_USER,

    /**
     * Teleportiert einen Benutzer zu einem anderen Benutzer. Die Aktion ist durchführbar, wenn sich der Benutzer, zu
     * dem sich teleportiert werden soll, in der Freundesliste befindet, oder beide Benutzer sich innerhalb eines
     * Kontextes befinden, für den der ausführende Benutzer die Berechtigung zur Teleportation besitzt. Befindet sich
     * der Benutzer, zu dem sich teleportiert werden soll in einem privaten Raum, ist für die Durchführung der Aktion
     * die Berechtigung zum Beitritt privater Räume ohne Zugangskontrolle erforderlich.
     */
    BAN_USER,

    /**
     * Meldet einen Benutzer. Benutzer mit der Berechtigung Benutzer sperren zu können. erhalten eine Benachrichtigung
     * mit der Information über gemeldete Benutzer. Wird ein Benutzer mit dieser Berechtigung gemeldet, erhalten nur
     * Benutzer mit der Berechtigung Moderatoren zu sperren eine Benachrichtigung. Benutzer mit der Berechtigung
     * Moderatoren zu sperren können nicht gemeldet werden.
     */
    UNBAN_USER,

    /**
     * Hebt die Stummschaltung eines Benutzers auf. Es werden alle Stummschaltungen innerhalb des größten Kontext
     * aufgehoben, für den der ausführende Benutzer die Berechtigung besitzt.
     */
    TELEPORT_TO_USER,

    /**
     * Sperrt einen Benutzer in der aktuellenWelt des ausführenden Benutzers, wenn er die Berechtigung dafür besitzt.
     * Befindet sich der gesperrte Benutzer gerade in dieser Welt, wird er dadurch aus dieser entfernt. Er kann dieser
     * Welt nicht mehr beitreten. Benutzer mit der Berechtigung Benutzer zu sperren, können nur von Benutzern mit der
     * Berechtigung Moderatoren zu sperren, gesperrt werden. Andere Benutzer mit der entsprechenden Berechtigung, sowie
     * der gesperrte Benutzer werden durch eine Benachrichtigung über die Aktion informiert. Benutzer mit der
     * Berechtigung Moderatoren zu sperren können nicht gesperrt werden.
     */
    ROOM_INVITE,

    /**
     * Entsperrt einen Benutzer in der aktuellen Welt des ausführenden Benutzers, wenn er die Berechtigung dafür
     * besitzt. Benutzer mit der Berechtigung Benutzer zu sperren, können nur von Benutzern mit der Berechtigung
     * Moderatoren zu sperren, entsperrt werden. Andere Benutzer mit der entsprechenden Berechtigung, sowie der
     * entsperrte Benutzer werden durch eine Benachrichtigung über die Aktion informiert.
     */
    ROOM_KICK,

    /**
     * Teilt einem Benutzer die Rolle des Moderators in der Welt des ausführenden Benutzers zu. Der Benutzer erhält eine
     * Benachrichtigung, in der ihm mitgeteilt wird, dass er diese Rolle erhalten hat.
     */
    ASSIGN_MODERATOR,

    /**
     * Entzieht einem Benutzer die Rolle des Moderators in der Welt des ausführenden Benutzers. Der Benutzer erhält eine
     * Benachrichtigung, in der ihm mitgeteilt wird, dass ihm diese Rolle entzogen wurde.
     */
    WITHDRAW_MODERATOR,

    /**
     * Teilt einem Benutzer die Rolle des Administrators zu. Der Benutzer erhält eine Benachrichtigung, in der ihm
     * mitgeteilt wird, dass er diese Rolle erhalten hat.
     */
    ASSIGN_ADMINISTRATOR,

    /**
     * Entzieht einem Benutzer die Rolle des Administrators, wenn der ausführende Benutzer die nötige Berechtigung dafür
     * besitzt. Der Benutzer erhält eine Benachrichtigung, in der ihm mitgeteilt wird, dass ihm diese Rolle entzogen
     * wurde.
     */
    WITHDRAW_ADMINISTRATOR,
}
