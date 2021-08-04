package model.user;

import model.MessageBundle;
import model.context.Context;
import model.context.ContextID;
import model.context.spatial.Music;
import model.context.spatial.SpatialContext;
import model.context.spatial.SpatialMap;
import model.exception.ContextNotFoundException;
import model.exception.NotificationNotFoundException;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Ein Interface, welche dem Controller Methoden zum Setzen von Parametern des am Client angemeldeten Benutzers zur
 * Verfügung stellt.
 */
public interface IInternUserController extends IUserController {

    /**
     * Setzt die Welt des internen Benutzers.
     * @param worldName Name der Welt.
     * @see SpatialContext
     */
    void joinWorld(ContextID worldId, String worldName);

    /**
     * Verwirft alle initialisierten Kontexte im Modell.
     */
    void leaveWorld();

    /**
     * Setzt den Raum des internen Benutzers und erzeugt die gesamte Kontexthierarchie anhand einer Karte.
     * @param roomId ID des Raums.
     * @param roomName Name des Raums.
     * @param map Karte des Raums, anhand der die Kontexthierarchie erzeugt wird.
     * @throws IllegalStateException wenn es keine aktuelle Welt gibt.
     * @see SpatialContext
     */
    void joinRoom(ContextID roomId, String roomName, SpatialMap map);

    /**
     * Setzt die Musik, die in einem Kontext abgespielt werden soll.
     * @param spatialId ID des räumlichen Kontextes, in dem die Musik abgespielt werden soll.
     * @param music Abzuspielende Musik.
     * @throws ContextNotFoundException falls dem Client kein Kontext mit der ID bekannt ist.
     * @see SpatialContext
     */
    void setMusic(ContextID spatialId, Music music) throws ContextNotFoundException;

    /**
     * Fügt dem Benutzer eine Benachrichtigung in einem Kontext hinzu.
     * @param contextId ID des Kontextes, in dem dem Benutzer die Benachrichtigung hinzugefügt werden soll.
     * @param notificationId ID der Benachrichtigung.
     * @param messageBundle Die übersetzbare Nachricht der Benachrichtigung zusammen mit ihren Argumenten.
     * @param timestamp Zeitstempel der Benachrichtigung.
     * @param isRequest true, wenn die Benachrichtigung als Anfrage dargestellt werden soll, sonst false.
     * @throws ContextNotFoundException wenn im Client kein Kontext mit der ID bekannt ist.
     * @throws IllegalStateException falls bei dem Benutzer bereits eine Benachrichtigung mit der ID hinterlegt ist.
     * @see Context
     */
    void addNotification(ContextID contextId, UUID notificationId, MessageBundle messageBundle, LocalDateTime timestamp,
                         boolean isRequest) throws ContextNotFoundException;

    /**
     * Entfernt eine Benachrichtigung des Benutzers.
     * @param notificationId ID der zu entfernenden Benachrichtigung
     * @throws NotificationNotFoundException wenn bei dem Benutzer keine Benachrichtigung mit der ID hinterlegt ist.
     */
    void removeNotification(UUID notificationId) throws NotificationNotFoundException;
}