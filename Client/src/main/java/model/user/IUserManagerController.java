package model.user;

import model.exception.UserNotFoundException;
import java.util.UUID;

/**
 * Ein Interface, welche dem Controller Methoden zum Zugriff auf den intern angemeldeten Benutzer, sowie externen
 * Benutzern zur Verfügung stellt.
 */
public interface IUserManagerController {

    /**
     * Setzt den intern angemeldeten Benutzer dieses Clients.
     * @param userId ID des internen Benutzers.
     */
    void setInternUser(UUID userId, String userName, Status status, Avatar avatar);

    /**
     * Fügt einen externen Benutzer in die Liste der bekannten externen Benutzer hinzu, falls dieser in der Liste noch
     * nicht enthalten ist.
     * @param userId ID des externen Benutzers.
     */
    void addExternUser(UUID userId, String userName, Status status, Avatar avatar);

    /**
     * Entfernt einen externen Benutzer aus der Liste der bekannten externen Benutzer.
     * @param userId ID des externen Benutzers.
     * @throws UserNotFoundException falls dem Client kein externer Benutzer mit der ID bekannt ist.
     */
    void removeExternUser(UUID userId) throws UserNotFoundException;

    /**
     * Gibt den intern angemeldeten Benutzer zurück.
     * @return Die Instanz des intern angemeldeten Benutzers.
     * @throws IllegalStateException falls kein Benutzer auf diesem Client angemeldet ist.
     */
    IInternUserController getInternUserController();

    /**
     * @param userId ID des externen Benutzers.
     * @return externer Benutzer.
     * @throws UserNotFoundException falls dem Client kein externer Benutzer mit der ID bekannt ist.
     */
    IUserController getExternUserController(UUID userId) throws UserNotFoundException;
}