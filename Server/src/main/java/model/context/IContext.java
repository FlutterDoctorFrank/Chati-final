package model.context;

import model.context.spatial.Music;
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
    ContextID getContextID();

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
     * Gibt die Musik, die innerhalb des Kontextes läuft, zurück.
     * @return Musik des Kontexts, wenn dieser eine hat, sonst null.
     */
    public Music getMusic();
}
