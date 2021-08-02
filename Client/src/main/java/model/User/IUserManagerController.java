package model.User;

import model.Exceptions.NotLoggedInException;
import model.Exceptions.UserNotFoundException;
import java.util.UUID;

/**
 * Eine Schnittstelle, welche dem Controller Methoden zum Zugriff auf den intern angemeldeten Benutzer,
 * sowie externen Benutzern zur Verfügung stellt.
 */
public interface IUserManagerController {
    /**
     * Setzt den intern angemeldeten Benutzer dieses Clients.
     * @param userId ID des internen Benutzers.
     */
    void setInternUser(UUID userId);

    /**
     * Fügt einen externen Benutzer in die Liste der bekannten externen Benutzer hinzu,
     * falls dieser in der Liste noch nicht enthalten ist.
     * @param userId ID des externen Benutzers.
     */
    void addExternUser(UUID userId);

    /**
     * Entfernt einen externen Benutzer aus der Liste der bekannten externen Benutzer.
     * @param userId ID des externen Benutzers.
     * @throws UserNotFoundException falls dem Client kein externer Benutzer mit
     * der ID bekannt ist.
     */
    void removeExternUser(UUID userId) throws UserNotFoundException;

    /**
     * Gibt den intern angemeldeten Benutzer zurück.
     * @return Die Instanz des intern angemeldeten Benutzers.
     * @throws NotLoggedInException falls kein Benutzer auf diesem Client angemeldet ist
     */
    IUserController getInternUser() throws NotLoggedInException;

    /**
     * @param userId ID des externen Benutzers.
     * @return externer Benutzer
     * @throws UserNotFoundException falls dem Client kein externer Benutzer mit
     * der ID bekannt ist.
     */
    IUserController getExternUser(UUID userId) throws UserNotFoundException;
}
