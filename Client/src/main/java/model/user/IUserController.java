package model.user;

import model.context.Context;
import model.context.spatial.ContextMap;
import model.context.spatial.Direction;
import model.context.spatial.Location;
import model.context.spatial.SpatialContext;
import model.exception.ContextNotFoundException;
import model.context.ContextID;
import model.role.Role;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

/**
 * Ein Interface, welche dem Controller Methoden zum Setzen von Parametern eines Benutzers zur Verfügung stellt.
 */
public interface IUserController {

    /**
     * Setzt den Benutzernamen eines Benutzers.
     * @param username Benutzername.
     */
    void setUsername(@NotNull final String username);

    /**
     * Setzt den Status eines Benutzers.
     * @param status Status.
     */
    void setStatus(@NotNull final Status status);

    /**
     * Setzt den Avatar eines Benutzers.
     * @param avatar Avatar.
     */
    void setAvatar(@NotNull final Avatar avatar);

    /**
     * Setzt die Welt des Benutzers.
     * @param worldId ID der Welt.
     * @throws ContextNotFoundException falls keine Welt mit der ID existiert.
     */
    void joinWorld(@NotNull final ContextID worldId) throws ContextNotFoundException;

    /**
     * Lässt den Benutzer die Welt verlassen.
     */
    void leaveWorld();

    /**
     * Setzt den Raum des Benutzers.
     * @param roomId ID des Raums.
     * @throws ContextNotFoundException falls kein Raum mit der ID existiert.
     */
    void joinRoom(@NotNull final ContextID roomId) throws ContextNotFoundException;

    /**
     * Lässt den Benutzer den Raum verlassen.
     */
    void leaveRoom();

    /**
     * Setzt die Information, ob sich dieser Benutzer in einem privaten Raum befindet.
     * @param isInPrivateRoom true, wenn sich der Benutzer in einem privaten Raum befindet, sonst false.
     * @see SpatialContext
     */
    void setInPrivateRoom(final boolean isInPrivateRoom);

    /**
     * Setzt die Information, ob ein Benutzer ein Freund des intern angemeldeten Benutzers ist.
     * @param isFriend true, wenn der Benutzer ein Freund ist, sonst false.
     */
    void setFriend(final boolean isFriend);

    /**
     * Setzt die Information, ob ein Benutzer vom intern angemeldeten Benutzer ignoriert wird.
     * @param isIgnored true, wenn der Benutzer ignoriert wird, sonst false.
     */
    void setIgnored(final boolean isIgnored);

    /**
     * Setzt die Information, ob der intern angemeldete Benutzer mit diesem Benutzer gerade kommunizieren kann.
     * @param canCommunicateWith true, wenn eine Kommunikation möglich ist, sonst false.
     */
    void setCommunicable(final boolean canCommunicateWith);

    /**
     * Setzt die Information, der sich der Benutzer aktuell bewegen darf.
     * @param isMovable true, wenn der Benutzer sich bewegen darf, sonst false.
     */
    void setMovable(final boolean isMovable);

    /**
     * Setzt die Information, ob ein Benutzer in einem bestimmten Kontext gemeldet ist.
     * @param contextId ID des Kontextes, in dem der Benutzer gemeldet ist.
     * @param isReported true, wenn der Benutzer in dem Kontext gemeldet ist, sonst false.
     * @throws ContextNotFoundException wenn im Client kein Kontext mit der ID bekannt ist.
     * @see Context
     */
    void setReport(@NotNull final ContextID contextId, final boolean isReported) throws ContextNotFoundException;

    /**
     * Setzt die Information, ob ein Benutzer in einem bestimmten Kontext stummgeschaltet ist.
     * @param contextId ID des Kontextes, in dem der Benutzer stummgeschaltet ist.
     * @param isMuted true, wenn der Benutzer in dem Kontext stummgeschaltet ist, sonst false.
     * @throws ContextNotFoundException wenn im Client kein Kontext mit der ID bekannt ist.
     * @see Context
     */
    void setMute(@NotNull final ContextID contextId, final boolean isMuted) throws ContextNotFoundException;

    /**
     * Setzt die Information, ob ein Benutzer in einem bestimmten Kontext gesperrt ist.
     * @param contextId ID des Kontextes, in dem der Benutzer gesperrt ist.
     * @param isBanned true, wenn der Benutzer in dem Kontext gesperrt ist, sonst false.
     * @throws ContextNotFoundException wenn im Client kein Kontext mit der ID bekannt ist.
     * @see Context
     */
    void setBan(@NotNull final ContextID contextId, final boolean isBanned) throws ContextNotFoundException;

    /**
     * Setzt die Rollen, die ein Benutzer in einem bestimmten Kontext hat.
     * @param contextId Setzt die Rollen, die ein Benutzer in einem bestimmten Kontext hat.
     * @param roles Menge der Rollen, die der Benutzer in dem Kontext hat.
     * @throws ContextNotFoundException wenn im Client kein Kontext mit der ID bekannt ist.
     * @see Context
     */
    void setRoles(@NotNull final ContextID contextId, @NotNull final Set<Role> roles) throws ContextNotFoundException;

    /**
     * Setzt die Position eines Benutzers.
     * @param posX X-Koordinate.
     * @param posY Y-Koordinate.
     * @param isTeleporting Information, ob der Benutzer zu seiner momentanen Position teleportiert werden soll.
     * @param isSprinting Information, ob der Benutzer sich schnell fortbewegt.
     * @param direction Richtung, in die der Avatar des Benutzers gerichtet sein soll.
     * @see Location
     */
    void setLocation(final float posX, final float posY, final boolean isTeleporting, final boolean isSprinting,
                     @NotNull final Direction direction);
}