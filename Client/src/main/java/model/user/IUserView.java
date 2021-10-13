package model.user;

import model.context.spatial.ILocationView;
import model.context.spatial.ISpatialContextView;
import model.context.spatial.Location;
import model.context.spatial.SpatialContext;
import model.role.Permission;
import model.role.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

/**
 * Ein Interface, welche der View Zugriff auf Parameter eines Benutzers zur Verfügung stellt.
 */
public interface IUserView {

    /**
     * Gibt die ID des Benutzers zurück.
     * @return ID des Benutzers
     */
    @NotNull UUID getUserId();

    /**
     * Gibt den aktuellen Benutzernamen des Benutzers zurück.
     * @return Benutzername des Benutzers
     */
    @NotNull String getUsername();

    /**
     * Gibt den aktuell ausgewählten Avatar des Benutzers zurück.
     * @return Avatar des Benutzers
     */
    @NotNull Avatar getAvatar();

    /**
     * Gibt den Status zurück, der für diesen Benutzer angezeigt werden soll.
     * @return Status des Benutzers.
     */
    @NotNull Status getStatus();

    /**
     * Gibt zurück, ob der Benutzer als angemeldet angezeigt werden soll.
     * @return true, wenn der Benutzer als angemeldet angezeigt werden soll, sonst false.
     */
    boolean isOnline();

    /**
     * Gibt zurück, ob der Benutzer als Freund markiert ist.
     * @return true, wenn der Benutzer als Freund markiert ist, sonst false.
     */
    boolean isFriend();

    /**
     * Gibt zurück, ob der Benutzer als ignoriert markiert ist.
     * @return true, wenn der Benutzer als ignoriert markiert ist, sonst false.
     */
    boolean isIgnored();

    /**
     * Gibt zurück, ob eine Flüsterkommunikation mit diesem Benutzer möglich ist.
     * @return true, wenn eine Flüsterkommunikation möglich ist, sonst false.
     */
    boolean canWhisper();

    /**
     * Gibt zurück, ob der intern angemeldete Benutzer diesen Benutzer in einen privaten Raum einladen kann.
     * @return true, wenn eine Einladung in den Raum möglich ist, sonst false.
     */
    boolean canBeInvited();

    /**
     * Gibt zurück, ob der intern angemeldete Benutzer diesen Benutzer aus einem privaten Raum entfernen kann.
     * @return true, wenn eine Entfernung möglich ist, sonst false.
     */
    boolean canBeKicked();

    /**
     * Gibt zurück, ob sich der intern angemeldete Benutzer zu diesem Benutzer teleportieren kann.
     * @return true, wenn eine Teleportation möglich ist, sonst false.
     */
    boolean canTeleportTo();

    /**
     * Gibt zurück, ob der intern angemeldete Benutzer diesen Benutzer melden kann.
     * @return true, wenn der Benutzer gemeldet werden kann, sonst false.
     */
    boolean canBeReported();

    /**
     * Gibt zurück, ob der intern angemeldete Benutzer diesen Benutzer stummschalten, oder die Stummschaltung aufheben
     * kann.
     * @return true, wenn dieser Benutzer stummgeschaltet, oder die Stummschaltung aufgehoben werden kann, sonst false.
     */
    boolean canBeMuted();

    /**
     * Gibt zurück, ob der intern angemeldete Benutzer diesen Benutzer sperren oder entsperren kann.
     * @return true, wenn dieser Benutzer gesperrt oder entsperrt werden kann, sonst false
     */
    boolean canBeBanned();

    /**
     * Gibt zurück, ob der intern angemeldete Benutzer diesem Benutzer die Rolle des Moderators erteilen oder entziehen
     * kann.
     * @return true, wenn diesem Benutzer die Rolle des Moderators erteilt oder entzogen werden kann.
     */
    boolean canAssignModerator();

    /**
     * Gibt zurück, ob der intern angemeldete Benutzer mit diesem Benutzer kommunizieren kann.
     * @return true, wenn eine Kommunikation möglich ist, sonst false.
     */
    boolean canCommunicateWith();

    /**
     * Gibt zurück, ob der Benutzer gerade Sprachnachrichten versenden darf.
     * @return true, wenn er Sprachnachrichten versenden darf, sonst false.
     */
    boolean canTalk();

    /**
     * Gibt zurück, ob der Benutzer Kameraaufnahmen versenden darf.
     * @return true, wenn er Kameraaufnahmen senden darf, sonst false.
     */
    boolean canShow();

    /**
     * Gibt zurück, ob der Benutzer Bildschirmaufnahmen versenden darf.
     * @return true, wenn er Bildschirmaufnahmen senden darf, sonst false.
     */
    boolean canShare();

    /**
     * Gibt zurück, ob der Benutzer im aktuellen räumlichen Kontext des internen Benutzers als gemeldet markiert ist.
     * @return true, wenn der Benutzer im aktuellen räumlichen Kontext als gemeldet markiert ist, sonst false.
     */
    boolean isReported();

    /**
     * Gibt zurück, ob der Benutzer im aktuellen räumlichen Kontext des internen Benutzers als stummgeschaltet markiert
     * ist.
     * @return true, wenn der Benutzer im aktuellen Kontext als stummgeschaltet markiert ist, sonst false.
     */
    boolean isMuted();

    /**
     * Gibt zurück, ob der Benutzer im aktuellen räumlichen Kontext des internen Benutzers als gesperrt markiert ist.
     * @return true, wenn der Benutzer im aktuellen Kontext als gesperrt markiert ist, sonst false.
     */
    boolean isBanned();

    /**
     * Gibt zurück, ob sich der Benutzer in der aktuellen Welt des internen Benutzers befindet.
     * @return true, wenn der Benutzer in derselben Welt ist, sonst false.
     * @see SpatialContext
     */
    boolean isInCurrentWorld();

    /**
     * Gibt zurück, ob sich der Benutzer im aktuellen Raum des internen Benutzers befindet.
     * @return true, wenn der Benutzer im selben Raum ist, sonst false.
     * @see SpatialContext
     */
    boolean isInCurrentRoom();

    /**
     * Gibt die aktuelle Welt des Benutzers zurück.
     * @return Aktuelle Welt des Benutzers.
     */
    @Nullable ISpatialContextView getCurrentWorld();

    /**
     * Gibt den aktuellen Raum des Benutzers zurück, sofern sich dieser in der aktuellen Welt des intern angemeldeten
     * Benutzers befindet.
     * @return Aktueller Raum des Benutzers.
     */
    @Nullable ISpatialContextView getCurrentRoom();

    /**
     * Gibt die aktuelle Position des Benutzers innerhalb des aktuellen Raumes zurück.
     * @return Die Position des Benutzers.
     * @see Location
     */
    @Nullable ILocationView getLocation();

    /**
     * Gibt die Information zurück, ob der Benutzer zu seiner aktuellen Position teleportiert, oder mit einer
     * Laufanimation bewegt werden soll.
     * @return true, wenn der Benutzer teleportiert werden soll, sonst false.
     */
    boolean isTeleporting();

    /**
     * Gibt die Information zurück, ob sich dieser Benutzer schnell fortbewegt.
     * @return true, wenn der Benutzer sich schnell fortbewegt, sonst false.
     */
    boolean isSprinting();

    /**
     * Gibt die Information zurück, ob sich dieser Benutzer bewegen darf.
     * @return true, wenn der Benutzer bewegen darf, sonst false.
     */
    boolean isMovable();

    /**
     * Gibt zurück, ob der Benutzer eine Rolle im innersten Kontext oder einem übergeordneten Kontext besitzt,
     * in dem sich der intern angemeldete Benutzer befindet.
     * @param role Zu überprüfende Rolle.
     * @return true, wenn der Benutzer die Rolle besitzt, sonst false.
     */
    boolean hasRole(@NotNull final Role role);

    /**
     * Gibt zurück, ob der Benutzer eine Berechtigung im innersten Kontext oder einem übergeordneten Kontext besitzt,
     * in dem sich der intern angemeldete Benutzer befindet.
     * @param permission Zu überprüfende Berechtigung.
     * @return true, wenn der Benutzer die Berechtigung besitzt, sonst false.
     */
    boolean hasPermission(@NotNull final Permission permission);

    /**
     * Gibt die übergeordnetste Rolle des Benutzers zurück, die er im innersten Kontext oder einem übergeordneten
     * Kontext besitzt, in dem sich der intern angemeldete Benutzer befindet. Die Rollen sind wie folgt hierarchisch
     * gegliedert: {@link Role#OWNER}, {@link Role#ADMINISTRATOR}, {@link Role#MODERATOR}, {@link Role#ROOM_OWNER},
     * {@link Role#AREA_MANAGER}, {@link Role#BOT}. Diese Gliederung wird für Anzeigezwecke genutzt und spiegelt keine
     * tatsächliche Hierarchie der Rollen wider, da nicht jede übergeordnete Rolle alle Berechtigungen aller
     * untergeordneter Rollen enthalten muss.
     * @return Übergeordnetste Rolle des Benutzers.
     */
    @Nullable Role getHighestRole();
}