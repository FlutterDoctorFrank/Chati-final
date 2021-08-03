package model.user.account;

import controller.network.ClientSender;
import model.exception.IllegalAccountActionException;
import model.exception.IllegalActionException;
import model.exception.UserNotFoundException;
import model.user.IUser;

import java.util.UUID;

/**
 * Ein Interface, welches dem Controller Methoden zur Verwaltung von Benutzerkonten bereitstellt. Wird von
 * {@link UserAccountManager} implementiert.
 */
public interface IUserAccountManager {

    /**
     * Erstellt ein Benutzerkonto.
     * @param username Der Benutzername, mit dem das Konto erstellt werden soll.
     * @param password Das Passwort, mit dem das Konto erstellt werden soll.
     * @throws IllegalAccountActionException wenn der übergebene Benutzername oder das Passwort das falsche Format
     * haben, oder ein Benutzer mit dem Benutzernamen bereits existiert.
     */
    void registerUser(String username, String password) throws IllegalAccountActionException;

    /**
     * Meldet einen Benutzer an.
     * @param username Der Benutzername des Benutzers, der angemeldet werden soll.
     * @param password Das Passwort des Benutzers, der angemeldet werden soll.
     * @param clientSender Der ClientSender des Benutzers.
     * @return Den eingeloggten Benutzer.
     * @throws IllegalAccountActionException wenn kein Benutzer mit dem übergebenen Benutzernamen existiert, das
     * Passwort nicht korrekt ist, oder der Benutzer bereits angemeldet ist.
     */
    IUser loginUser(String username, String password, ClientSender clientSender) throws IllegalAccountActionException;

    /**
     * Meldet einen Benutzer ab.
     * @param userId Die ID des Benutzers, der abgemeldet werden soll.
     * @throws UserNotFoundException wenn kein Benutzer mit der ID existiert.
     */
    void logoutUser(UUID userId) throws UserNotFoundException;

    /**
     * Löscht ein Benutzerkonto. Meldet den entsprechenden Benutzer automatisch ab,
     * wenn dieser angemeldet ist.
     * @param userId Die ID des Benutzers, dessen Konto gelöscht werden soll.
     * @param password Das Passwort des Benutzers, dessen Konto gelöscht werden soll.
     * @throws UserNotFoundException wenn kein Benutzer mit der ID existiert.
     * @throws IllegalAccountActionException wenn das übergebene Passwort nicht korrekt ist.
     */
    void deleteUser(UUID userId, String password) throws UserNotFoundException, IllegalAccountActionException;

    /**
     * Ändert das Passwort eines Benutzers.
     * @param userId Die ID des Benutzers, dessen Passwort geändert werden soll.
     * @param password Das aktuelle Passwort des Benutzers.
     * @param newPassword Das neue Passwort des Benutzers.
     * @throws UserNotFoundException wenn kein Benutzer mit der ID existiert.
     * @throws IllegalAccountActionException wenn das aktuelle Passwort nicht korrekt ist oder das neue Passwort nicht
     * das richtige Format hat.
     */
    void changePassword(UUID userId, String password, String newPassword) throws UserNotFoundException, IllegalAccountActionException;
}