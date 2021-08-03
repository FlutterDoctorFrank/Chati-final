package model.context;

import model.user.IUser;

import java.util.Map;
import java.util.UUID;

/**
 * Ein Interface, welche dem Controller Methoden zur Verwaltung von Kontexten bereitstellt. Wird von {@link Context}
 * implementiert.
 */
public interface IContext {

    /**
     * Gibt die ID des Kontexts zurück.
     * @return ID des Kontexts.
     */
    ContextID getContextId();

    /**
     * Gibt den Namen des Kontexts zurück.
     * @return Name des Kontextes.
     */
    String getContextName();

    /**
     * Gibt die Benutzer zurück, die sich gerade innerhalb dieses Kontexts befinden.
     * @return Menge aller Benutzer im Kontext.
     */
    Map<UUID, IUser> getIUsers();

    /**
     * Gibt die Benutzer zurück, die in diesem Kontext gemeldet sind.
     * @return Gemeldete Benutzer.
     */
    Map<UUID, IUser> getReportedUsers();

    /**
     * Gibt die Benutzer zurück, die in diesem Kontext stummgeschaltet sind.
     * @return Stummgeschaltete Benutzer.
     */
    Map<UUID, IUser> getMutedUsers();

    /**
     * Gibt die Benutzer zurück, die in diesem Kontext gesperrt sind.
     * @return Gesperrte Benutzer.
     */
    Map<UUID, IUser> getBannedUsers();
}