package model.user;

import model.context.Context;
import model.context.spatial.Location;
import model.context.spatial.SpatialContext;
import model.exception.IllegalActionException;
import model.exception.NotificationNotFoundException;
import model.context.ContextID;
import model.context.spatial.SpatialContextType;
import model.role.Role;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Eine Schnittstelle, welche dem Controller Methoden zum Setzen von Parametern eines
 * Benutzers zur Verfügung stellt.
 */
public interface IUserController {
    /**
     * Setzt den Benutzernamen eines Benutzers.
     * @param username Benutzername
     */
    void setUsername(String username);

    /**
     * Setzt den Status eines Benutzers
     * @param status Status
     */
    void setStatus(Status status);

    /**
     * Setzt den Avatar eines Benutzers.
     * @param avatar Avatar
     */
    void setAvatar(Avatar avatar);

    /**
     * Setzt die Information, ob ein Benutzer sich in der aktuellen Welt des intern angemeldeten Benutzers bendet.
     * @param inWorld true, wenn der Benutzer in der aktuellen Welt ist,
     * sonst false.
     * @see SpatialContext
     * @see SpatialContextType#WORLD
     */
    void setCurrentWorld(boolean inWorld);

    /**
     * Setzt die Information, ob ein Benutzer sich im aktuellen Raum des intern angemeldeten Benutzers bendet und somit auf
     * der aktuell auf dem Client gezeigten Karte
     * dargestellt werden soll.
     * @param inRoom true, wenn der Benutzer im aktuellen Raum ist,
     * sonst false.
     * @see SpatialContext
     * @see SpatialContextType#ROOM
     */
    void setCurrentRoom(boolean inRoom);

    /**
     * Setzt die Information, ob ein Benutzer ein Freund des intern angemeldeten Benutzers ist.
     * @param isFriend true, wenn der Benutzer ein Freund ist, sonst false.
     */
    void setFriend(boolean isFriend);

    /**
     * Setzt die Information, ob ein Benutzer vom intern angemeldeten Benutzer ignoriert
     * wird.
     * @param isIgnored true, wenn der Benutzer ignoriert wird, sonst false.
     */
    void setIgnored(boolean isIgnored);

    /**
     * Setzt die Information, ob ein Benutzer in einem bestimmten Kontext gemeldet ist.
     * @param contextId ID des Kontextes, in dem der Benutzer gemeldet ist.
     * @param isReported true, wenn der Benutzer in dem Kontext gemeldet ist, sonst
     * false.
     * @see Context
     */
    void setReport(ContextID contextId, boolean isReported);

    /**
     * Setzt die Information, ob ein Benutzer in einem bestimmten Kontext stummgeschaltet ist
     * @param contextId ID des Kontextes, in dem der Benutzer stummgeschaltet ist.
     * @param isMuted true, wenn der Benutzer in dem Kontext stummgeschaltet ist,
     * sonst false.
     * @see Context
     */
    void setMute(ContextID contextId, boolean isMuted);

    /**
     * Setzt die Information, ob ein Benutzer in einem bestimmten Kontext gesperrt ist.
     * @param contextId ID des Kontextes, in dem der Benutzer gesperrt ist.
     * @param isBanned true, wenn der Benutzer in dem Kontext gesperrt ist, sonst
     * false.
     * @see Context
     */
    void setBan(ContextID contextId, boolean isBanned);

    /**
     * Setzt die Rollen, die ein Benutzer in einem bestimmten Kontext hat.
     * @param contextId Setzt die Rollen, die ein Benutzer in einem bestimmten Kontext hat.
     * @param roles Menge der Rollen, die der Benutzer in dem Kontext hat.
     * @see Context
     */
    void setRoles(ContextID contextId, Set<Role> roles);

    /**
     * Fügt dem Benutzer eine Benachrichtigung in einem Kontext hinzu.
     * @param contextId: ID des Kontextes, in dem dem Benutzer die Benachrichtigung hinzugefügt werden soll.
     * @param notificationId: ID der Benachrichtigung.
     * @param messageKey: Kennung der Nachricht der Benachrichtigung.
     * @param args: Mögliche Argumente der Benachrichtigung, wie beispielsweise der
     * Name des anfragenden oder verursachenden Benutzers oder eine von einem Benutzer hinzugefügte Nachricht.
     * @param timestamp: Zeitstempel der Benachrichtigung.
     * @param isRequest: true, wenn die Benachrichtigung als Anfrage dargestellt werden soll, sonst false.
     * @throws IllegalActionException : falls bei dem Benutzer bereits eine Benachrichtigung mit der ID hinterlegt ist.
     * @see Context
     */
    void addNotification(ContextID contextId, UUID notificationId, String messageKey, Object[] args,
                         LocalDateTime timestamp, boolean isRequest) throws IllegalActionException;

    /**
     * Entfernt eine Benachrichtigung des Benutzers.
     * @param notificationId ID der zu entfernenden Benachrichtigung
     * throws NoticationNotFoundException: wenn bei dem Benutzer keine Benachrichtigung mit der ID hinterlegt ist.
     */
    void removeNotification(UUID notificationId) throws NotificationNotFoundException;


    /**
     * Setzt die Position eines Benutzers.
     * @param posX X-Koordinate
     * @param posY Y-Koordinate
     * @see Location
     */
    void setPosition(int posX, int posY);
}
