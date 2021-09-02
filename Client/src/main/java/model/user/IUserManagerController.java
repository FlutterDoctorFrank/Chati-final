package model.user;

import model.exception.UserNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
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
    void login(@NotNull final UUID userId, @NotNull final String userName,
               @NotNull final Status status, @NotNull final Avatar avatar);

    /**
     * Verwirft alle initialisierten Benutzer und Kontexte im Modell.
     */
    void logout();

    /**
     * Fügt einen externen Benutzer in die Liste der bekannten externen Benutzer hinzu, falls dieser in der Liste noch
     * nicht enthalten ist.
     * @param userId ID des externen Benutzers.
     */
    void addExternUser(@NotNull final UUID userId, @NotNull final String userName,
                       @NotNull final Status status, @Nullable final Avatar avatar);

    /**
     * Entfernt einen externen Benutzer aus der Liste der bekannten externen Benutzer.
     * @param userId ID des externen Benutzers.
     * @throws UserNotFoundException falls dem Client kein externer Benutzer mit der ID bekannt ist.
     */
    void removeExternUser(@NotNull final UUID userId) throws UserNotFoundException;

    /**
     * Gibt den intern angemeldeten Benutzer zurück.
     * @return Die Instanz des intern angemeldeten Benutzers.
     * @throws IllegalStateException falls kein Benutzer auf diesem Client angemeldet ist.
     */
    @NotNull IInternUserController getInternUserController();

    /**
     * @param userId ID des externen Benutzers.
     * @return externer Benutzer.
     * @throws UserNotFoundException falls dem Client kein externer Benutzer mit der ID bekannt ist.
     */
    @NotNull IUserController getExternUserController(@NotNull final UUID userId) throws UserNotFoundException;

    /**
     * Gibt die externen Benutzer zurück.
     * @return die externen Benutzer.
     */
    @NotNull Map<UUID, IUserController> getExternUsers();
}