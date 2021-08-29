package model.user;

import model.context.Context;
import model.context.spatial.Location;
import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import model.context.ContextID;
import model.role.Role;

import java.util.Set;

/**
 * Ein Interface, welche dem Controller Methoden zum Setzen von Parametern eines Benutzers zur Verfügung stellt.
 */
public interface IUserController {

    /**
     * Setzt den Benutzernamen eines Benutzers.
     * @param username Benutzername.
     */
    void setUsername(String username);

    /**
     * Setzt den Status eines Benutzers.
     * @param status Status.
     */
    void setStatus(Status status);

    /**
     * Setzt den Avatar eines Benutzers.
     * @param avatar Avatar.
     */
    void setAvatar(Avatar avatar);

    /**
     * Setzt die Information, ob ein Benutzer sich in der aktuellen Welt des intern angemeldeten Benutzers befindet.
     * @param isInCurrentWorld true, wenn der Benutzer in der aktuellen Welt ist, sonst false.
     * @see SpatialContext
     */
    void setInCurrentWorld(boolean isInCurrentWorld);

    /**
     * Setzt die Information, ob ein Benutzer sich im aktuellen Raum des intern angemeldeten Benutzers bendet und somit
     * auf der aktuell auf dem Client gezeigten Karte dargestellt werden soll.
     * @param isInCurrentRoom true, wenn der Benutzer im aktuellen Raum ist, sonst false.
     * @see SpatialContext
     */
    void setInCurrentRoom(boolean isInCurrentRoom);

    /**
     * Setzt die Information, ob sich dieser Benutzer in einem privaten Raum befindet.
     * @param isInPrivateRoom true, wenn sich der Benutzer in einem privaten Raum befindet, sonst false.
     * @see SpatialContext
     */
    void setInPrivateRoom(boolean isInPrivateRoom);

    /**
     * Setzt die Information, ob ein Benutzer ein Freund des intern angemeldeten Benutzers ist.
     * @param isFriend true, wenn der Benutzer ein Freund ist, sonst false.
     */
    void setFriend(boolean isFriend);

    /**
     * Setzt die Information, ob ein Benutzer vom intern angemeldeten Benutzer ignoriert wird.
     * @param isIgnored true, wenn der Benutzer ignoriert wird, sonst false.
     */
    void setIgnored(boolean isIgnored);

    /**
     * Setzt die Information, ob der intern angemeldete Benutzer mit diesem Benutzer gerade kommunizieren kann.
     * @param canCommunicateWith true, wenn eine Kommunikation möglich ist, sonst false.
     */
    void setCommunicable(boolean canCommunicateWith);

    /**
     * Setzt die Information, ob ein Benutzer in einem bestimmten Kontext gemeldet ist.
     * @param contextId ID des Kontextes, in dem der Benutzer gemeldet ist.
     * @param isReported true, wenn der Benutzer in dem Kontext gemeldet ist, sonst false.
     * @throws ContextNotFoundException wenn im Client kein Kontext mit der ID bekannt ist.
     * @see Context
     */
    void setReport(ContextID contextId, boolean isReported) throws ContextNotFoundException;

    /**
     * Setzt die Information, ob ein Benutzer in einem bestimmten Kontext stummgeschaltet ist.
     * @param contextId ID des Kontextes, in dem der Benutzer stummgeschaltet ist.
     * @param isMuted true, wenn der Benutzer in dem Kontext stummgeschaltet ist, sonst false.
     * @throws ContextNotFoundException wenn im Client kein Kontext mit der ID bekannt ist.
     * @see Context
     */
    void setMute(ContextID contextId, boolean isMuted) throws ContextNotFoundException;

    /**
     * Setzt die Information, ob ein Benutzer in einem bestimmten Kontext gesperrt ist.
     * @param contextId ID des Kontextes, in dem der Benutzer gesperrt ist.
     * @param isBanned true, wenn der Benutzer in dem Kontext gesperrt ist, sonst false.
     * @throws ContextNotFoundException wenn im Client kein Kontext mit der ID bekannt ist.
     * @see Context
     */
    void setBan(ContextID contextId, boolean isBanned) throws ContextNotFoundException;

    /**
     * Setzt die Rollen, die ein Benutzer in einem bestimmten Kontext hat.
     * @param contextId Setzt die Rollen, die ein Benutzer in einem bestimmten Kontext hat.
     * @param roles Menge der Rollen, die der Benutzer in dem Kontext hat.
     * @throws ContextNotFoundException wenn im Client kein Kontext mit der ID bekannt ist.
     * @see Context
     */
    void setRoles(ContextID contextId, Set<Role> roles) throws ContextNotFoundException;

    /**
     * Setzt die Position eines Benutzers.
     * @param posX X-Koordinate.
     * @param posY Y-Koordinate.
     * @param isTeleporting Information, ob der Benutzer zu seiner momentanen Position teleportiert werden soll.
     * @param isSprinting Information, ob der Benutzer sich schnell fortbewegt.
     * @see Location
     */
    void setLocation(float posX, float posY, boolean isTeleporting, boolean isSprinting);
}