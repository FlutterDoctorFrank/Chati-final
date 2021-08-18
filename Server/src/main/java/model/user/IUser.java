package model.user;

import model.context.ContextID;
import model.context.spatial.ILocation;
import model.context.spatial.IWorld;
import model.context.spatial.objects.Interactable;
import model.exception.*;
import model.notification.INotification;
import model.role.IContextRole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder bereits in einer Welt ist.
     * @throws ContextNotFoundException wenn keine Welt mit der ID existiert.
     * @throws IllegalWorldActionException wenn der Benutzer in der Welt gesperrt ist.
     * @see model.context.spatial.World
     */
    void joinWorld(@NotNull final ContextID worldId) throws ContextNotFoundException, IllegalWorldActionException;

    /**
     * Lässt den Benutzer seine aktuelle Welt verlassen.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder nicht in einer Welt ist.
     */
    void leaveWorld() throws IllegalStateException;

    /**
     * Verändert die Position eines Benutzers.
     * @param posX Neue X-Koordinate.
     * @param posY Neue Y-Koordinate.
     * @throws IllegalPositionException wenn die übergebenen Koordinaten ungültig sind oder eine Kollision verursachen.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder nicht in einer Welt ist.
     * @see model.context.spatial.Location
     */
    void move(final int posX, final int posY) throws IllegalPositionException;

    /**
     * Sendet eine Nachricht im Namen des Benutzers, von dem sie erhalten wurde gemäß des entsprechenden Nachrichtentyps
     * und der geltenden Kommunikationsform an andere Benutzer.
     * @param message Die vom Benutzer erhaltene Nachricht.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder nicht in einer Welt ist.
     * @see model.communication.message.TextMessage
     * @see model.communication.CommunicationRegion
     */
    void chat(@NotNull final String message);

    /**
     * Sendet eine Sprachnachricht im Namen des Benutzers, von dem sie erhalten wurde gemäß der geltenden
     * Kommunikationsform an andere Benutzer.
     * @param voiceData Audiodaten.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder nicht in einer Welt ist.
     * @see model.communication.message.VoiceMessage
     * @see model.communication.CommunicationRegion
     */
    void talk(final byte[] voiceData);

    /**
     * Führt im Namen des Benutzers eine administrative Aktion auf einen anderen Benutzer aus.
     * @param targetId ID des Benutzers, auf den die Aktion ausgeführt werden soll.
     * @param action Auszuführende Aktion.
     * @param args Argumente der durchzuführenden Aktion.
     * @throws UserNotFoundException wenn kein Benutzer mit der ID existiert.
     * @throws NoPermissionException wenn der durchführende Benutzer nicht die nötige Berechtigung besitzt.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder nicht in einer Welt ist oder eine nicht
     * durchführbare Aktion durchgeführt werden soll.
     */
    void executeAdministrativeAction(@NotNull final UUID targetId, @NotNull final AdministrativeAction action,
                                     @NotNull final String[] args) throws UserNotFoundException, NoPermissionException;

    /**
     * Lässt den Benutzer mit einem Kontext interagieren.
     * @param interactableId ID des Kontexts, mit dem interagiert werden soll.
     * @throws IllegalInteractionException wenn der Benutzer sich nicht in unmittelbarer Nähe eines Kontexts mit der ID
     * befindet, mit dem eine Interaktion möglich ist, oder der Benutzer gerade bereits mit einem anderen Objekt
     * interagiert.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder nicht in einer Welt ist.
     * @see Interactable
     */
    void interact(@NotNull final ContextID interactableId) throws IllegalInteractionException, ContextNotFoundException;

    /**
     * Lässt den Benutzer eine Menüoption eines Kontexts durchführen.
     * @param interactableId ID des Kontexts, dessen Menü-Option ausgeführt werden soll.
     * @param menuOption Menü-Option die ausgeführt werden soll.
     * @param args Argumente, mit denen die Menü-Option ausgeführt werden soll.
     * @throws ContextNotFoundException wenn kein (interagierbarer) Kontext mit der ID in der Nähe des Benutzers
     * gefunden wurde.
     * @throws IllegalInteractionException wenn der Benutzer sich nicht in unmittelbarer Nähe eines Kontexts mit der ID
     * befindet, mit dem eine Interaktion möglich ist, dieses kein Menü hat, oder der Benutzer nicht das Menü dieses
     * Kontextes geöffnet hat.
     * @throws IllegalMenuActionException wenn der Kontext nicht die Menü-Option unterstützt oder die Argumente ungültig
     * sind.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder nicht in einer Welt ist.
     * @see Interactable
     */
    void executeOption(@NotNull final ContextID interactableId, final int menuOption,
                       @NotNull final String[] args) throws ContextNotFoundException, IllegalInteractionException, IllegalMenuActionException;

    /**
     * Löscht eine Benachrichtigung des Benutzers.
     * @param notificationId ID der zu löschenden Benachrichtigung.
     * @throws NotificationNotFoundException wenn der Benutzer keine Benachrichtigung mit der ID besitzt.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet ist.
     * @see model.notification.Notification
     */
    void deleteNotification(@NotNull final UUID notificationId) throws NotificationNotFoundException;

    /**
     * Akzeptiert die in einer Benachrichtigung enthaltenen Anfrage, oder lehnt diese ab.
     * @param notificationId ID der Benachrichtigung, dessen Anfrage akzeptiert
     * oder abgelehnt werden soll.
     * @param accept Falls true, wird die Anfrage akzeptiert, sonst wird sie abgelehnt.
     * @throws NotificationNotFoundException wenn der Benutzer keine Benachrichtigung
     * mit der ID besitzt.
     * @throws IllegalNotificationActionException wenn die Benachrichtigung keine
     * Anfrage ist.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet ist.
     * @see model.notification.Notification
     */
    void manageNotification(@NotNull final UUID notificationId, final boolean accept) throws NotificationNotFoundException,
            IllegalNotificationActionException;

    /**
     * Ändert den Avatar des Benutzers.
     * @param avatar Neuer Avatar des Benutzers.
     */
    void setAvatar(@NotNull final Avatar avatar);

    /**
     * Gibt die ID des Benutzers zurück.
     * @return ID des Benutzers.
     */
    @NotNull UUID getUserId();

    /**
     * Gibt den Benutzernamen des Benutzers zurück.
     * @return Benutzername des Benutzers.
     */
    @NotNull String getUsername();

    /**
     * Gibt den aktuellen Status des Benutzers zurück.
     * @return Status des Benutzers.
     */
    @NotNull Status getStatus();

    /**
     * Gibt den aktuell ausgewählten Avatar des Benutzers zurück.
     * @return Avatar des Benutzers.
     */
    @NotNull Avatar getAvatar();

    /**
     * Gibt die aktuelle Welt, in der sich der Benutzer bendet, zurück.
     * @return Aktuelle Welt des Benutzers.
     */
    @Nullable IWorld getWorld();

    /**
     * Gibt die aktuelle Position innerhalb des Raumes, in dem sich der Benutzer befindet, zurück.
     * @return Aktuelle Position des Benutzers.
     */
    @Nullable ILocation getLocation();

    /**
     * Gibt die Freunde des Benutzers zurück.
     * @return Menge der Freunde des Benutzers.
     */
    @NotNull Map<UUID, IUser> getFriends();

    /**
     * Gibt die von diesem Benutzer ignorierten Benutzer zurück.
     * @return Menge der vom Benutzer ignorierten Benutzer.
     */
    @NotNull Map<UUID, IUser> getIgnoredUsers();

    /**
     * Gibt die Rollen des Benutzers im globalen Kontext zurück.
     * @return Menge der globalen Rollen des Benutzers.
     */
    @NotNull IContextRole getGlobalRoles();

    /**
     * Gibt die Benachrichtigungen des Benutzers im globalen Kontext zurück.
     * @return Menge der globalen Benachrichtigungen des Benutzers.
     */
    @NotNull Map<UUID, INotification> getGlobalNotifications();
}