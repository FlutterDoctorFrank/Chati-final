package model.database;

import model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * Eine Schnittstelle, welche einem UserAccountManager Methoden zur Verfügung stellt, um Datenbankzugriffe
 * durchzuführen.
 */
public interface IUserAccountManagerDatabase {

    /**
     * Legt ein Benutzerkonto an.
     * @param username Benutzername des anzulegenden Kontos.
     * @param password Passwort des anzulegenden Kontos.
     * @return Den Benutzer, falls die Erstellung des Benutzerkonto erfolgreich war, null sonst.
     */
    @Nullable User createAccount(@NotNull final String username, @NotNull final String password);

    /**
     * Überprüft die Korrektheit des Passworts des Benutzerkontos mit dem übergebenen Benutzernamen.
     * @param username Benutzername des Kontos.
     * @param password Vom Benutzer eingegebenes Passwort.
     * @return die ID des Benutzers, wenn das eingegebene Passwort korrekt ist, sonst null.
     */
    boolean checkPassword(@NotNull final String username, @NotNull final String password);

    /**
     * Ändert das Passwort eines Benutzers.
     * @param user Benutzer, dessen Passwort geändert werden soll.
     * @param newPassword Neues Passwort des Benutzers.
     */
    void setPassword(@NotNull final User user, @NotNull final String newPassword);

    /**
     * Aktualisiert die Zeit der letzten Aktivität eines Benutzers.
     * @param user Benutzer, dessen Zeit der letzten Aktivität aktualisiert werden soll.
     */
    void updateLastOnlineTime(@NotNull final User user);

    /**
     * Entfernt ein Benutzerkonto aus der Datenbank.
     * @param user ID des Benutzers, dessen Konto gelöscht werden soll.
     */
    void deleteAccount(@NotNull final User user);

    /**
     * Gibt den Benutzer mit der übergebenen ID zurück.
     * @param userId ID des zurückzugebenden Benutzers.
     * @return Benutzer.
     */
    @Nullable User getUser(@NotNull final UUID userId);

    /**
     * Gibt den Benutzer mit dem übergebenen Benutzernamen zurück.
     * @param username Benutzername des zurückzugebenden Benutzers.
     * @return Benutzer.
     */
    @Nullable User getUser(@NotNull final String username);

    /**
     * Gibt alle Benutzer zurück
     * @return alle Benutzer.
     */
    @NotNull Map<UUID, User> getUsers();
}
