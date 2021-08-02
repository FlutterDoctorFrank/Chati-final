package model.user;

import model.context.ContextID;
import model.context.IContext;
import model.context.spatial.ILocation;
import model.context.spatial.IWorld;
import model.exception.*;
import model.notification.INotification;
import model.role.IContextRole;

import java.util.Map;
import java.util.UUID;

/**
 * Ein Interface, welches dem Controller Methoden zur Durchführung von Benutzeraktionen und zum Zugriff auf
 * Benutzerdaten bereitstellt. Wird von {@link User} implementiert.
 */
public interface IUser {

    /**
     * Lässt den Benutzer eine Welt betreten.
     * @param worldId Die ID der zu betretenden Welt.
     * @throws IllegalStateException wenn der Benutzer bereits in einer Welt ist.
     * @throws ContextNotFoundException wenn keine Welt mit der ID existiert.
     * @throws IllegalWorldActionException wenn der Benutzer in der Welt gesperrt ist.
     * @see model.context.spatial.Area
     */
    void joinWorld(ContextID worldId) throws IllegalStateException, ContextNotFoundException, IllegalWorldActionException;

    /**
     * Lässt den Benutzer seine aktuelle Welt verlassen.
     * @throws IllegalStateException wenn der Benutzer in keiner Welt ist.
     */
    void leaveWorld() throws IllegalStateException;

    /**
     * Verändert die Position eines Benutzers.
     * @param posX Neue X-Koordinate.
     * @param posY Neue Y-Koordinate.
     * @throws IllegalPositionException wenn die übergebenen Koordinaten ungültig sind oder eine Kollision verursachen.
     * @throws IllegalStateException wenn der Benutzer nicht in einer Welt ist.
     * @see model.context.spatial.Location
     */
    void tryMove(int posX, int posY) throws IllegalPositionException, IllegalStateException;

    /**
     * Sendet eine Nachricht im Namen des Benutzers, von dem sie erhalten wurde gemäß
     * des entsprechenden Nachrichtentyps und der geltenden Kommunikationsform an
     * andere Benutzer.
     * @param message Die vom Benutzer erhaltene Nachricht.
     * @see model.communication.message.TextMessage
     * @see model.communication.CommunicationRegion
     */
    void chat(String message);

    /**
     * Sendet eine Sprachnachricht im Namen des Benutzers, von dem sie erhalten wurde
     * gemäß der geltenden Kommunikationsform an andere Benutzer.
     * @param voicedata Audiodaten.
     * @see model.communication.message.VoiceMessage
     * @see model.communication.CommunicationRegion
     */
    void talk(byte[] voicedata);

    /**
     * Führt im Namen des Benutzers eine administrative Aktion auf einen anderen Benutzer
     * aus.
     * @param targetId ID des Benutzers, auf den die Aktion ausgeführt werden soll.
     * @param action Auszuführende Aktion.
     * @param args Argumente der durchzuführenden Aktion.
     * @throws UserNotFoundException wenn kein Benutzer mit der ID existiert.
     * @throws NoPermissionException wenn der durchführende Benutzer nicht die nötige Berechtigung besitzt.
     * @throws IllegalStateException wenn eine nicht durchführbare Aktion durchgeführt werden soll.
     */
    void executeAdministrativeAction(UUID targetId, AdministrativeAction action, String[] args) throws UserNotFoundException, NoPermissionException, IllegalStateException;

    /**
     * Lässt den Benutzer mit einem Kontext interagieren.
     * @param interactableId ID des Kontexts, mit dem interagiert werden soll.
     * @throws IllegalInteractionException wenn der Benutzer sich nicht in unmittelbarer Nähe eines Kontexts mit der ID
     * befindet, mit dem eine Interaktion möglich ist, oder der Benutzer gerade bereits mit einem anderen Objekt
     * interagiert.
     * @see model.context.spatial.Interactable
     */
    void interact(ContextID interactableId) throws IllegalInteractionException;

    /**
     * Lässt den Benutzer eine Menüoption eines Kontexts durchführen.
     * @param interactableId ID des Kontexts, dessen Menü-Option ausgeführt werden soll.
     * @param menuOption Menü-Option die ausgeführt werden soll.
     * @param args Argumente, mit denen die Menü-Option ausgeführt werden soll.
     * @throws IllegalInteractionException wenn der Benutzer sich nicht in unmittelbarer Nähe eines Kontexts mit der ID
     * befindet, mit dem eine Interaktion möglich ist, dieses kein Menü hat, oder der Benutzer nicht das Menü dieses
     * Kontextes geöffnet hat.
     * @throws IllegalMenuActionException wenn der Kontext nicht die Menü-Option unterstützt oder die Argumente ungültig
     * sind.
     * @see model.context.spatial.Interactable
     */
    void executeOption(ContextID interactableId, int menuOption, String[] args) throws IllegalInteractionException, IllegalMenuActionException;

    /**
     * Löscht eine Benachrichtigung des Benutzers.
     * @param notificationId ID der zu löschenden Benachrichtigung.
     * @throws NotificationNotFoundException wenn der Benutzer keine Benachrichtigung mit der ID besitzt.
     * @see model.notification.Notification
     */
    void deleteNotification(UUID notificationId) throws NotificationNotFoundException;

    /**
     * Akzeptiert die in einer Benachrichtigung enthaltenen Anfrage, oder lehnt diese ab.
     * @param notificationId ID der Benachrichtigung, dessen Anfrage akzeptiert
     * oder abgelehnt werden soll.
     * @param accept Falls true, wird die Anfrage akzeptiert, sonst wird sie abgelehnt.
     * @throws NotificationNotFoundException wenn der Benutzer keine Benachrichtigung
     * mit der ID besitzt.
     * @throws IllegalNotificationActionException wenn die Benachrichtigung keine
     * Anfrage ist.
     * @see model.notification.Notification
     */
    void manageNotification(UUID notificationId, boolean accept) throws NotificationNotFoundException, IllegalNotificationActionException;

    /**
     * Ändert den Avatar des Benutzers.
     * @param avatar Neuer Avatar des Benutzers.
     */
    void setAvatar(Avatar avatar);

    /**
     * Gibt die ID des Benutzers zurück.
     * @return ID des Benutzers.
     */
    UUID getUserId();

    /**
     * Gibt den Benutzernamen des Benutzers zurück.
     * @return Benutzername des Benutzers.
     */
    String getUsername();

    /**
     * Gibt den aktuellen Status des Benutzers zurück.
     * @return Status des Benutzers.
     */
    Status getStatus();

    /**
     * Gibt den aktuell ausgewählten Avatar des Benutzers zurück.
     * @return Avatar des Benutzers.
     */
    Avatar getAvatar();

    /**
     * Gibt die aktuelle Welt, in der sich der Benutzer bendet, zurück.
     * @return Aktuelle Welt des Benutzers.
     */
    IWorld getWorld();

    /**
     * Gibt die aktuelle Position innerhalb des Raumes, in dem sich der Benutzer bendet,
     * zurück.
     * @return Aktuelle Position des Benutzers.
     */
    ILocation getLocation();

    /**
     * Gibt die Freunde des Benutzers zurück.
     * @return Menge der Freunde des Benutzers.
     */
    Map<UUID, IUser> getFriends();

    /**
     * Gibt die von diesem Benutzer ignorierten Benutzer zurück.
     * @return Menge der vom Benutzer ignorierten Benutzer.
     */
    Map<UUID, IUser> getIgnoredUsers();

    /**
     * Gibt die Rollen des Benutzers im globalen Kontext zurück.
     * @return Menge der globalen Rollen des Benutzers.
     */
    Map<IContext, IContextRole> getGlobalRoles();

    /**
     * Gibt die Benachrichtigungen des Benutzers im globalen Kontext zurück.
     * @return Menge der globalen Benachrichtigungen des Benutzers.
     */
    Map<UUID, INotification> getGlobalNotifications();
}
