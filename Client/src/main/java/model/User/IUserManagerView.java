package model.User;

import model.Exceptions.NotInWorldException;
import model.Exceptions.NotLoggedInException;
import model.Exceptions.UserNotFoundException;
import java.util.Map;
import java.util.UUID;

/**
 * Eine Schnittstelle, welche der View Methoden zum Zugri auf den intern angemeldeten
 * sowie auf die externen Benutzer zur Verfügung stellt.
 */
public interface IUserManagerView {
    /**
     * Gibt den intern angemeldeten Benutzer zurück.
     * @return Die Instanz des intern angemeldeten Benutzers.
     * @throws NotLoggedInException falls kein Benutzer auf diesem Client angemeldet ist
     */
    public IUserView getInternUserView() throws NotLoggedInException;

    /**
     * Gibt einen externen Benutzer zurück.
     * @return ID des externen Benutzers.
     * @throws UserNotFoundException falls dem Client kein externer Benutzer mit
     * der ID bekannt ist.
     */
    public IUserView getExternUserView(UUID userId) throws UserNotFoundException;

    /**
     * Gibt die externen Benutzer zurück, die in der aktuellen Welt als aktiv hinterlegt
     * sind.
     * @return Aktive Benutzer der aktuellen Welt.
     * @throws NotInWorldException wenn es keine aktuelle Welt gibt.
     */
    public Map<UUID, IUserView> getActiveUsers() throws NotInWorldException;

    /**
     * Gibt die externen Benutzer zurück, mit denen der interne Benutzer befreundet ist.
     * @return externer Benutzer
     * @throws NotLoggedInException falls kein Benutzer auf diesem Client angemeldet ist
     */
    public Map<UUID, IUserView> getFriends() throws NotLoggedInException;

    /**
     * Gibt die externen Benutzer zurück, die in der aktuellen Welt als gesperrt hinterlegt
     * sind.
     * @return externer Benutzer
     * @throws NotInWorldException wenn es keine aktuelle Welt gibt.
     */
    public Map<UUID, IUserView> getBannedUsers() throws NotInWorldException;
}
