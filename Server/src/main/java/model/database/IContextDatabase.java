package model.database;

import model.context.Context;
import model.context.ContextID;
import model.context.spatial.AreaReservation;
import model.context.spatial.World;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

/**
 * Eine Schnittstelle, welche Context Methoden zur Verfügung stellt, um Datenbankzugriffe durchzuführen.
 */
public interface IContextDatabase {

    /**
     * Fügt eine Welt in der Datenbank hinzu.
     * @param world Hinzuzufügende Welt.
     */
    void addWorld(@NotNull final World world);

    /**
     * Entfernt eine Welt aus der Datenbank.
     * @param world Zu entfernende Welt.
     */
    void removeWorld(@NotNull final World world);

    /**
     * Lädt die Welt mit der angegebenen Kontext-ID aus der Datenbank und gibt sie zurück.
     * @param worldID ID der Welt
     * @return Welt mit der ID.
     */
    @Nullable World getWorld(@NotNull final ContextID worldID);

    /**
     * Lädt alle in der Datenbank hinterlegten Welten und gibt sie zurück.
     * @return Menge aller in der Datenbank hinterlegten Welten.
     */
    @NotNull Map<ContextID, World> getWorlds();

    /**
     * Setzt die Liste der gesperrten Benutzer für eine Welt.
     * @param world Die Welt.
     */
    void getBannedUsers(@NotNull final World world);

    /**
     * Fügt die Sperrung eines Benutzers in einer Welt in der Datenbank hinzu.
     * @param user Benutzer, dem der gesperrt werden soll.
     * @param world Welt, in der Benutzer gesperrt werden soll.
     */
    void addBannedUser(@NotNull final User user, @NotNull final Context world);

    /**
     * Entfernt die Sperrung eines Benutzers in einer Welt aus der Datenbank.
     * @param user Benutzer, der entsperrt werden soll.
     * @param world Welt, in der Benutzer entsperrt werden soll.
     */
    void removeBannedUser(@NotNull final User user, @NotNull final Context world);

    /**
     * Setzt die Reservierungen zum Erhalt der Rolle des Bereichsberechtigten für eine Welt.
     * @param world Die Welt
     */
    void getAreaReservations(@NotNull final World world);

    /**
     * Fügt eine Reservierung der Rolle des Bereichsberechtigten in der Datenbank hinzu.
     * @param areaReservation Hinzuzufügende Reservierung.
     */
    void addAreaReservation(@NotNull final AreaReservation areaReservation);

    /**
     * Entfernt eine Reservierung der Rolle des Bereichsberechtigten aus der Datenbank.
     * @param areaReservation Zu entfernende Reservierung.
     */
    void removeAreaReservation(@NotNull final AreaReservation areaReservation);
}
