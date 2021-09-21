package model.user;

import model.context.ContextID;
import model.context.spatial.ISpatialContextView;
import model.exception.UserNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * Ein Interface, welche der View Methoden zum Zugriff auf den intern angemeldeten
 * sowie auf die externen Benutzer zur Verfügung stellt.
 */
public interface IUserManagerView {

    /**
     * Gibt den intern angemeldeten Benutzer zurück.
     * @return Die Instanz des intern angemeldeten Benutzers.
     * @throws IllegalStateException falls kein Benutzer auf diesem Client angemeldet ist.
     */
    @Nullable IInternUserView getInternUserView();

    /**
     * Gibt einen externen Benutzer mit der übergebenen ID zurück.
     * @param userId ID des externen Benutzers.
     * @return Externer Benutzer.
     * @throws UserNotFoundException falls dem Client kein externer Benutzer mit der ID bekannt ist.
     */
    @NotNull IUserView getExternUserView(@NotNull final UUID userId) throws UserNotFoundException;

    /**
     * Gibt einen externen Benutzer mit dem übergebenen Namen zurück.
     * @param username Name des externen Benutzers.
     * @return Externer Benutzer
     * @throws UserNotFoundException falls dem Client kein externer Benutzer mit dem Namen bekannt ist.
     */
    @NotNull IUserView getExternUserView(@NotNull final String username) throws UserNotFoundException;

    /**
     * Gibt die externen Benutzer zurück, die in der aktuellen Welt als aktiv hinterlegt sind.
     * @return Aktive Benutzer der aktuellen Welt.
     * @throws IllegalStateException wenn es keine aktuelle Welt gibt.
     */
    @NotNull Map<UUID, IUserView> getActiveUsers();

    /**
     * Gibt die externen Benutzer zurück, mit denen der interne Benutzer befreundet ist.
     * @return Externe Benutzer, mit denen der interne Benutzer befreundet ist.
     * @throws IllegalStateException falls kein Benutzer auf diesem Client angemeldet ist.
     */
    @NotNull Map<UUID, IUserView> getFriends();

    /**
     * Gibt die externen Benutzer zurück, die in der aktuellen Welt als gesperrt hinterlegt sind.
     * @return Externe Benutzer, die in der aktuellen Welt als gesperrt hinterlegt sind.
     * @throws IllegalStateException wenn es keine aktuelle Welt gibt.
     */
    @NotNull Map<UUID, IUserView> getBannedUsers();

    /**
     * Gibt die externen Benutzer zurück, die sich gerade in dem Raum des intern angemeldeten Benutzers befinden.
     * @return Externe Benutzer, die sich im aktuellen Raum befinden.
     * @throws IllegalStateException wenn es keine aktuelle Welt gibt.
     */
    @NotNull Map<UUID, IUserView> getUsersInRoom();

    /**
     * Gibt die externen Benutzer zurück, mit denen der intern angemeldete Benutzer gerade kommunizieren kann.
     * @return Externe Benutzer, mit denen der intern angemeldete Benutzer gerade kommunizieren kann.
     * @throws IllegalStateException wenn es keine aktuelle Welt gibt.
     */
    @NotNull Map<UUID, IUserView> getCommunicableUsers();

    /**
     * Gibt zurück, ob auf diesem Client gerade ein Benutzer angemeldet ist.
     * @return true, wenn ein Benutzer angemeldet ist, sonst false.
     */
    boolean isLoggedIn();

    /**
     * Gibt die Menge aller Welten zurück.
     * @return Menge aller Welten.
     */
    @NotNull Map<ContextID, ISpatialContextView> getWorlds();

    /**
     * Gibt die Menge aller privaten Räume in der aktuellen Welt des intern angemeldeten Benutzers zurück.
     * @return Menge aller privaten Räume.
     */
    @NotNull Map<ContextID, ISpatialContextView> getPrivateRooms();
}