package model.user;

import model.communication.message.AudioMessage;
import model.context.ContextID;
import model.context.spatial.Direction;
import model.context.spatial.ILocation;
import model.context.spatial.IWorld;
import model.context.spatial.objects.Interactable;
import model.exception.ContextNotFoundException;
import model.exception.IllegalAdministrativeActionException;
import model.exception.IllegalInteractionException;
import model.exception.IllegalMenuActionException;
import model.exception.IllegalNotificationActionException;
import model.exception.IllegalPositionException;
import model.exception.IllegalWorldActionException;
import model.exception.NoPermissionException;
import model.exception.NotificationNotFoundException;
import model.exception.UserNotFoundException;
import model.notification.INotification;
import model.notification.NotificationAction;
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
     * @param direction Neue Richtung.
     * @param posX Neue X-Koordinate.
     * @param posY Neue Y-Koordinate.
     * @param isSprinting Information, ob sich der Benutzer gerade schnell fortbewegt.
     * @throws IllegalPositionException wenn die übergebenen Koordinaten ungültig sind oder eine Kollision verursachen.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder nicht in einer Welt ist.
     * @see model.context.spatial.Location
     */
    void move(@NotNull final Direction direction, final float posX, final float posY, final boolean isSprinting) throws IllegalPositionException;

    /**
     * Signalisiert, dass der Benutzer gerade am tippen ist.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder nicht in einer Welt ist.
     */
    void type() throws IllegalStateException;

    /**
     * Sendet eine Nachricht im Namen des Benutzers, von dem sie erhalten wurde gemäß des entsprechenden Nachrichtentyps
     * und der geltenden Kommunikationsform an andere Benutzer.
     * @param message Die vom Benutzer erhaltene Nachricht.
     * @param imageData Die vom Benutzer erhaltenen Bilddaten.
     * @param imageName Der Name des vom Benutzer erhaltenen Bildes.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder nicht in einer Welt ist.
     * @see model.communication.message.TextMessage
     * @see model.communication.CommunicationRegion
     */
    void chat(@NotNull final String message, final byte[] imageData, @Nullable final String imageName);

    /**
     * Sendet eine Sprachnachricht im Namen des Benutzers, von dem sie erhalten wurde gemäß der geltenden
     * Kommunikationsform an andere Benutzer.
     * @param voiceData Audiodaten.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder nicht in einer Welt ist.
     * @see AudioMessage
     * @see model.communication.CommunicationRegion
     */
    void talk(final byte[] voiceData);

    /**
     * Sendet einen Videoframe von der Kamera des Benutzers, von dem es erhalten wurde gemäß der geltenden
     * Kommunikationsform an andere Benutzer.
     * @param frameData Daten des Frames.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder nicht in einer Welt ist.
     * @see model.communication.message.VideoFrame
     * @see model.communication.CommunicationRegion
     */
    void show(final byte[] frameData);

    /**
     * Sendet einen Videoframe einer Bildschirmaufnahme des Benutzers, von dem es erhalten wurde gemäß der geltenden
     * Kommunikationsform an andere Benutzer.
     * @param frameData Daten des Frames.
     */
    void share(final byte[] frameData);

    /**
     * Führt im Namen des Benutzers eine administrative Aktion auf einen anderen Benutzer aus.
     * @param targetId ID des Benutzers, auf den die Aktion ausgeführt werden soll.
     * @param action Auszuführende Aktion.
     * @param args Argumente der durchzuführenden Aktion.
     * @throws UserNotFoundException wenn kein Benutzer mit der ID existiert.
     * @throws NoPermissionException wenn der durchführende Benutzer nicht die nötige Berechtigung besitzt.
     * @throws IllegalAdministrativeActionException wenn eine nicht durchführbare Aktion durchgeführt werden soll.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet oder nicht in einer Welt ist.
     */
    void executeAdministrativeAction(@NotNull final UUID targetId, @NotNull final AdministrativeAction action,
                                     @NotNull final String[] args) throws UserNotFoundException, IllegalAdministrativeActionException, NoPermissionException;

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
     * Behandelt eine Benachrichtigung.
     * @param notificationId ID der Benachrichtigung, dessen Anfrage akzeptiert
     * oder abgelehnt werden soll.
     * @param action Die Aktion, die auf der Benachrichtigung ausgeführt werden soll.
     * @throws NotificationNotFoundException wenn der Benutzer keine Benachrichtigung
     * mit der ID besitzt.
     * @throws IllegalNotificationActionException wenn die Benachrichtigung keine
     * Anfrage ist.
     * @throws IllegalStateException wenn der Benutzer nicht angemeldet ist.
     * @see model.notification.Notification
     * @see NotificationAction
     */
    void manageNotification(@NotNull UUID notificationId, NotificationAction action) throws NotificationNotFoundException,
            IllegalNotificationActionException;

    /**
     * Ändert den Avatar des Benutzers.
     * @param avatar Neuer Avatar des Benutzers.
     */
    void setAvatar(@NotNull final Avatar avatar);

    /**
     * Ändert den Status des Benutzers.
     * @param status Neuer Status des Benutzers.
     */
    void setStatus(@NotNull final Status status);

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
     * Gibt die Information zurück, ob sich der Benutzer schnell fortbewegt.
     * @return true, wenn sich der Benutzer schnell fortbewegt, sonst false.
     */
    boolean isSprinting();

    /**
     * Gibt die Information zurück, ob sich der Benutzer bewegen kann.
     * @return true, wenn sich der Benutzer bewegen kann, sonst false.
     */
    boolean isMovable();

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
     * Gibt die Benutzer zurück, mit denen der Benutzer gerade kommunizieren kann.
     * @return Menge der Benutzer, mit denen dieser Benutzer gerade kommunizieren kann.
     */
    @NotNull Map<UUID, IUser> getCommunicableIUsers();

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