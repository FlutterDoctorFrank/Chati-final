package model.user;

import model.MessageBundle;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.ContextMusic;
import model.context.spatial.SpatialContext;
import model.context.spatial.ContextMap;
import model.exception.ContextNotFoundException;
import model.exception.NotificationNotFoundException;
import model.notification.NotificationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Ein Interface, welche dem Controller Methoden zum Setzen von Parametern des am Client angemeldeten Benutzers zur
 * Verfügung stellt.
 */
public interface IInternUserController extends IUserController {

    /**
     * Setzt den Raum des internen Benutzers und erzeugt die gesamte Kontexthierarchie anhand einer Karte.
     * @param roomId ID des Raums.
     * @param roomName Name des Raums.
     * @param map Karte des Raums, anhand der die Kontexthierarchie erzeugt wird.
     * @throws ContextNotFoundException wenn es keinen Raum mit der gegebenen ID gibt.
     * @throws IllegalStateException wenn es keine aktuelle Welt gibt.
     * @see SpatialContext
     */
    void joinRoom(@NotNull final ContextID roomId, @NotNull final String roomName,
                  @NotNull final ContextMap map) throws ContextNotFoundException;

    /**
     * Setzt die Musik, die in einem Kontext abgespielt werden soll.
     * @param spatialId ID des räumlichen Kontextes, in dem die Musik abgespielt werden soll.
     * @param music Abzuspielende Musik.
     * @param looping Information, ob Musik wiederholt abgespielt werden soll.
     * @param random Information, ob nach Beenden eines Musikstücks ein zufälliges nächstes abgespielt werden soll.
     * @throws ContextNotFoundException falls dem Client kein Kontext mit der ID bekannt ist.
     * @see SpatialContext
     */
    void setMusic(@NotNull final ContextID spatialId, @Nullable final ContextMusic music, final boolean looping,
                  final boolean random) throws ContextNotFoundException;

    /**
     * Fügt dem Benutzer eine Benachrichtigung in einem Kontext hinzu.
     * @param contextId ID des Kontextes, in dem dem Benutzer die Benachrichtigung hinzugefügt werden soll.
     * @param notificationId ID der Benachrichtigung.
     * @param messageBundle Die übersetzbare Nachricht der Benachrichtigung zusammen mit ihren Argumenten.
     * @param timestamp Zeitstempel der Benachrichtigung.
     * @param type Art der Benachrichtigung.
     * @param isRead Die Information, ob diese Benachrichtigung als gelesen markiert werden soll.
     * @param isAccepted Die Information, ob diese Benachrichtigung als akzeptiert markiert werden soll.
     * @param isDeclined Die Information, ob diese Benachrichtigung als abgelehnt markiert werden soll.
     * @throws ContextNotFoundException wenn im Client kein Kontext mit der ID bekannt ist.
     * @throws IllegalStateException falls bei dem Benutzer bereits eine Benachrichtigung mit der ID hinterlegt ist.
     * @see Context
     */
    void addNotification(@NotNull final ContextID contextId, @NotNull final UUID notificationId,
                         @NotNull final MessageBundle messageBundle, @NotNull final LocalDateTime timestamp,
                         @NotNull final NotificationType type, final boolean isRead,
                         final boolean isAccepted, final boolean isDeclined) throws ContextNotFoundException;

    /**
     * Aktualisiert Informationen einer vorhandenen Benachrichtigung.
     * @param notificationId ID der zu aktualisierenden Benachrichtigung.
     * @param isRead Die Information, ob diese Benachrichtigung als gelesen markiert werden soll.
     * @param isAccepted Die Information, ob diese Benachrichtigung als akzeptiert markiert werden soll.
     * @param isDeclined Die Information, ob diese Benachrichtigung als abgelehnt markiert werden soll.
     * @throws NotificationNotFoundException wenn bei dem Benutzer keine Benachrichtigung mit der ID hinterlegt ist.
     */
    void updateNotification(@NotNull final UUID notificationId, final boolean isRead,
                            final boolean isAccepted, final boolean isDeclined) throws NotificationNotFoundException;

    /**
     * Entfernt eine Benachrichtigung des Benutzers.
     * @param notificationId ID der zu entfernenden Benachrichtigung
     * @throws NotificationNotFoundException wenn bei dem Benutzer keine Benachrichtigung mit der ID hinterlegt ist.
     */
    void removeNotification(@NotNull final UUID notificationId) throws NotificationNotFoundException;
}