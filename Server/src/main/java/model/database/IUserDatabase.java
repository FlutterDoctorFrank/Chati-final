package model.database;

import model.context.Context;
import model.notification.Notification;
import model.role.Role;
import model.user.Avatar;
import model.user.User;
import org.jetbrains.annotations.NotNull;

/**
 * Ein Interface, welches einem User Methoden zur Verfügung stellt, um Datenbankzugriffe durchzuführen.
 */
public interface IUserDatabase {

    /**
     * Setzt den Avatar des Benutzers.
     * @param user Der Benutzer
     * @param avatar Der Avatar
     */
    void changeAvatar(@NotNull final User user, @NotNull final Avatar avatar);

    /**
     * Fügt die Freundschaft der Benutzer in der Datenbank hinzu.
     * @param first Erster Benutzer.
     * @param second Zweiter Benutzer.
     */
    void addFriendship(@NotNull final User first, @NotNull final User second);

    /**
     * Entfernt die Freundschaft der Benutzer aus der Datenbank.
     * @param first Erster Benutzer.
     * @param second Zweiter Benutzer.
     */
    void removeFriendship(@NotNull final User first, @NotNull final User second);

    /**
     * Fügt einen ignorierten Benutzer zu einem Benutzer in der Datenbank hinzu.
     * @param ignoringUser Ignorierender Benutzer.
     * @param ignoredUser Ignorierter Benutzer.
     */
    void addIgnoredUser(@NotNull final User ignoringUser, @NotNull final User ignoredUser);

    /**
     * Entfernt einen ignorierten Benutzer eines Benutzers aus der Datenbank.
     * @param ignoringUser Ignorierender Benutzer.
     * @param ignoredUser Ignorierter Benutzer.
     */
    void removeIgnoredUser(@NotNull final User ignoringUser, @NotNull final User ignoredUser);

    /**
     * Fügt dem Benutzer eine Rolle in der Datenbank hinzu.
     * @param user Benutzer, dem die Rolle hinzugefügt werden soll.
     * @param context Context.
     * @param role Hinzuzufügende Rolle.
     */
    void addRole(@NotNull final User user, @NotNull final Context context, @NotNull final Role role);

    /**
     * Benutzer, dessen Rolle entfernt werden soll.
     * @param user Benutzer, dessen Rolle entfernt werden soll.
     * @param context Context.
     * @param role Die Rolle, die entfernt werden soll.
     */
    void removeRole(@NotNull final User user, @NotNull final Context context, @NotNull final Role role);

    /**
     * Fügt dem Benutzer eine Benachrichtigung in der Datenbank hinzu.
     * @param user Benutzer, dem die Benachrichtigung hinzugefügt werden soll.
     * @param notification Hinzuzufügende Benachrichtigung.
     */
    void addNotification(@NotNull final User user, @NotNull final Notification notification);

    /**
     * Aktualisiert die Notification eines Benutzers, falls diese existiert.
     * @param user Der Benutzer, dem die Notifikation gehört.
     * @param notification Die Notification.
     */
    void updateNotification(@NotNull final User user, @NotNull final Notification notification);

    /**
     * Entfernt eine Benachrichtigung des Benutzers aus der Datenbank.
     * @param user Benutzer, dessen Benachrichtigung entfernt werden soll.
     * @param notification Benachrichtigung, die entfernt werden soll.
     */
    void removeNotification(@NotNull final User user, @NotNull final Notification notification);
}
