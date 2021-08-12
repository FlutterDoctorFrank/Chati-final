package model.user;

import model.exception.UserNotFoundException;
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
    IInternUserView getInternUserView();

    /**
     * Gibt einen externen Benutzer zurück.
     * @return ID des externen Benutzers.
     * @throws UserNotFoundException falls dem Client kein externer Benutzer mit der ID bekannt ist.
     */
    IUserView getExternUserView(UUID userId) throws UserNotFoundException;

    /**
     * Gibt die externen Benutzer zurück, die in der aktuellen Welt als aktiv hinterlegt sind.
     * @return Aktive Benutzer der aktuellen Welt.
     * @throws IllegalStateException wenn es keine aktuelle Welt gibt.
     */
    Map<UUID, IUserView> getActiveUsers();

    /**
     * Gibt die externen Benutzer zurück, mit denen der interne Benutzer befreundet ist.
     * @return Externe Benutzer, mit denen der interne Benutzer befreundet ist.
     * @throws IllegalStateException falls kein Benutzer auf diesem Client angemeldet ist.
     */
    Map<UUID, IUserView> getFriends();

    /**
     * Gibt die externen Benutzer zurück, die in der aktuellen Welt als gesperrt hinterlegt sind.
     * @return Externe Benutzer, die in der aktuellen Welt als gesperrt hinterlegt sind.
     * @throws IllegalStateException wenn es keine aktuelle Welt gibt.
     */
    Map<UUID, IUserView> getBannedUsers();

    /**
     * Gibt zurück, ob auf diesem Client gerade ein Benutzer angemeldet ist.
     * @return true, wenn ein Benutzer angemeldet ist, sonst false.
     */
    boolean isLoggedIn();
}