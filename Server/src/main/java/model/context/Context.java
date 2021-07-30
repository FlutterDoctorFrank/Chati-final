package model.context;

import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.spatial.Music;
import model.context.spatial.SpatialContext;
import model.database.Database;
import model.database.IContextDatabase;
import model.user.IUser;
import model.user.User;

import java.util.*;

/**
 * Eine Klasse, welche einen Kontext der Anwendung repräsentiert. Ein Kontext legt den
 * Umfang fest, in welchem Benutzer Benachrichtigungen empfangen können, Rollen haben
 * können, kommunizieren können und sonstige Benutzeraktionen durchführen können.
 */
public abstract class Context implements IContext {

    /** Dient der eindeutigen Identifikation eines Kontextes.*/
    protected final ContextID contextID;

    /** Der Name eines Kontextes. */
    protected final String contextName;

    /** Der übergeordnete Kontext. */
    protected final Context parent;

    /** Die Menge aller untergeordneter (räumlicher) Kontexte. */
    protected final Map<ContextID, SpatialContext> children;

    /** Die in diesem Kontext enthaltenen Benutzer. */
    private final Map<UUID, User> containedUsers;

    /** Die in diesem Kontext gemeldeten Benutzer. */
    private final Map<UUID, User> reportedUsers;

    /** Die in diesem Kontext stummgeschalteten Benutzer. */
    private final Map<UUID, User> mutedUsers;

    /** Die in diesem Kontext gesperrten Benutzer. */
    private final Map<UUID, User> bannedUsers;

    /** Die in diesem Kontext geltende Kommunikationsform. */
    private final CommunicationRegion communicationRegion;

    /** Die in diesem Kontext verwendbaren Kommunikationsmedien. */
    private final Set<CommunicationMedium> communicationMedia;

    /** Die in diesem Kontext laufende Musik.*/
    private Music music;

    /** Ermöglicht Zugriff auf die Datenbank */
    protected final IContextDatabase database;

    /**
     * Erzeugt eine Instanz eines Kontextes.
     * @param contextName Name des Kontextes.
     * @param parent Übergeordneter Kontext.
     * @param communicationRegion Die Kommunikationsform des Kontextes.
     * @param communicationMedia Die verwendbaren Kommunikationsmedien des Kontextes.
     */
    protected Context(String contextName, Context parent, CommunicationRegion communicationRegion,
                      Set<CommunicationMedium> communicationMedia) {
        if (parent == null) {
            this.contextID = new ContextID(contextName);
        } else {
            this.contextID = new ContextID(parent.getContextID().getId().concat(".").concat(contextName));
        }
        this.contextName = contextName;
        this.parent = parent;
        this.children = new HashMap<>();
        this.containedUsers = new HashMap<>();
        this.reportedUsers = new HashMap<>();
        this.mutedUsers = new HashMap<>();
        this.bannedUsers = new HashMap<>();
        this.communicationRegion = communicationRegion;
        this.communicationMedia = communicationMedia;
        this.music = null;
        this.database = Database.getContextDatabase();
    }

    @Override
    public ContextID getContextID() {
        return contextID;
    }

    @Override
    public String getContextName() {
        return contextName;
    }

    @Override
    public Map<UUID, IUser> getIUsers() {
        return Collections.unmodifiableMap(containedUsers);
    }

    @Override
    public Map<UUID, IUser> getReportedUsers() {
        return Collections.unmodifiableMap(reportedUsers);
    }

    @Override
    public Map<UUID, IUser> getMutedUsers() {
        return Collections.unmodifiableMap(mutedUsers);
    }

    @Override
    public Map<UUID, IUser> getBannedUsers() {
        return Collections.unmodifiableMap(bannedUsers);
    }

    @Override
    public Music getMusic() {
        return music;
    }

    /**
     * Fügt einen untergeordneten räumlichen Kontext hinzu.
     * @param child Untergeordneter Kontext.
     */
    public void addChild(SpatialContext child) {
        children.put(child.getContextID(), child);
    }

    /**
     * Fügt einen Benutzer in einen Kontext hinzu. Stellt sicher, dass dieser Benutzer auch in den übergeordneten
     * Kontexten hinzugefügt wird, sofern er dort nicht enthalten ist.
     * @param user Hinzuzufügender Benutzer.
     */
    public void addUser(User user) {
        containedUsers.put(user.getUserId(), user);
        if (parent != null && !parent.contains(user)) {
            parent.addUser(user);
        } else {
            // send new User music information
            user.getClientSender().send(ClientSender.SendAction.CONTEXT_MUSIC, this);
        }
    }

    /**
     * Entfernt einen Benutzer aus einem Kontext. Stellt sicher, dass dieser Benutzer auch aus allen untergeordneten
     * Kontexten entfernt wird, sofern er dort enthalten ist.
     * @param user Zu entfernender Benutzer.
     */
    public void removeUser(User user) {
        containedUsers.remove(user.getUserId());
        children.values().forEach(child -> {
            if (child.contains(user)) {
                child.removeUser(user);
            }
        });
    }

    /**
     * Fügt einen Benutzer zu den gemeldeten Benutzern in diesem Kontext hinzu.
     * @param user Hinzuzufügender Benutzer.
     */
    public void addReportedUser(User user) {
        reportedUsers.put(user.getUserId(), user);
        user.updateUserInfo();
    }

    /**
     * Entfernt einen Benutzer aus den gemeldeten Benutzern in diesem Kontext. Stellt sicher, dass der Benutzer aus
     * allen untergeordneten Kontexten als gemeldeter Benutzer entfernt wird.
     * @param user Zu entfernender Benutzer.
     */
    public void removeReportedUser(User user) {
        reportedUsers.remove(user.getUserId());
        children.values().forEach(child -> {
            if (child.isReported(user)) {
                child.removeReportedUser(user);
            }
        });
        user.updateUserInfo();
    }

    /**
     * Fügt einen Benutzer zu den stummgeschalteten Benutzern in diesem Kontext hinzu. Stellt sicher, dass der Benutzer
     * in allen untergeordneten Kontexten als stummgeschalteter Benutzer enthalten ist.
     * @param user Hinzuzufügender Benutzer.
     */
    public void addMutedUser(User user) {
        mutedUsers.put(user.getUserId(), user);
        children.values().forEach(child -> {
            if (!child.isMuted(user)) {
                child.addMutedUser(user);
            }
        });
        user.updateUserInfo();
    }

    /**
     * Entfernt einen Benutzer aus den stummgeschalteten Benutzern in diesem Kontext. Stellt sicher, dass der Benutzer
     * aus allen untergeordneten Kontexten als gemeldeter Benutzer entfernt wird.
     * @param user Zu entfernender Benutzer.
     */
    public void removeMutedUser(User user) {
        mutedUsers.remove(user.getUserId());
        children.values().forEach(child -> {
            if (child.isMuted(user)) {
                child.removeMutedUser(user);
            }
        });
        user.updateUserInfo();
    }

    /**
     * Fügt einen Benutzer zu den gesperrten Benutzern in diesem Kontext hinzu.
     * @param user Hinzuzufügender Benutzer.
     */
    public void addBannedUser(User user) {
        bannedUsers.put(user.getUserId(), user);
        database.addBannedUser(user, this);
        user.updateUserInfo();
    }

    /**
     * Entfernt einen Benutzer aus den gesperrten Benutzern in diesem Kontext. Stellt sicher, dass der Benutzer aus
     * allen untergeordneten Kontexten als gemeldeter Benutzer entfernt wird.
     * @param user Zu entfernender Benutzer.
     */
    public void removeBannedUser(User user) {
        bannedUsers.remove(user.getUserId());
        children.values().forEach(child -> {
            if (child.isBanned(user)) {
                child.removeBannedUser(user);
            }
        });
        database.removeBannedUser(user, this);
        user.updateUserInfo();
    }

    /**
     * Setzt die momentan abzuspielende Musik in diesem Kontext.
     * @param music Abzuspielende Musik.
     */
    public void playMusic(Music music) {
        this.music = music;
        // Sende Information über geänderte Musik an alle Benutzer im Kontext.
        containedUsers.values().forEach(user -> {
            user.getClientSender().send(ClientSender.SendAction.CONTEXT_MUSIC, this);
        });
    }

    /**
     * Stoppt die momentan abzuspielende Musik in diesem Kontext.
     */
    public void stopMusic() {
        playMusic(null);
    }

    /**
     * Überprüft, ob ein Benutzer in diesem Kontext enthalten ist.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer enthalten ist, sonst false.
     */
    public boolean contains(User user) {
        return containedUsers.containsKey(user.getUserId());
    }

    /**
     * Überprüft, ob ein Benutzer in diesem Kontext, oder einem übergeordneten Kontext, gemeldet ist.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer gemeldet ist, sonst false.
     */
    public boolean isReported(User user) {
        return reportedUsers.containsKey(user.getUserId()) || parent.isReported(user);
    }

    /**
     * Überprüft, ob ein Benutzer in diesem Kontext, oder einem übergeordneten Kontext stummgeschaltet ist.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer stummgeschaltet ist, sonst false.
     */
    public boolean isMuted(User user) {
        return mutedUsers.containsKey(user.getUserId()) || parent.isMuted(user);
    }

    /**
     * Überprüft, ob ein Benutzer in diesem Kontext, oder einem übergeordneten Kontext gesperrt ist.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer gesperrt ist, sonst false.
     */
    public boolean isBanned(User user) {
        return bannedUsers.containsKey(user.getUserId()) || parent.isBanned(user);
    }

    /**
     * Überprüft, ob der übergebene Kontext ein übergeordneter Kontext ist.
     * @param context Zu überprüfender Kontext.
     * @return true, wenn der übergebene Kontext übergeordnet ist, sonst false.
     */
    public boolean isInContext(Context context) {
        return equals(context) || parent.isInContext(context);
    }

    /**
     * Ermittelt den letzten gemeinsamen Vorfahren (= übergeordneten Kontext), dieses Kontextes und des übergebenen
     * Kontextes.
     * @param context Zu überprüfender Kontext.
     * @return Letzter gemeinsamer Vorfahre beider Kontexte.
     */
    public Context lastCommonAncestor(Context context) {
        return isInContext(context) ? context : (context.isInContext(this) ? this : parent.lastCommonAncestor(context));
    }

    /**
     * Ermittelt alle Benutzer, mit denen ein übergebener Benutzer in diesem Kontext kommunizieren kann.
     * @param communicatingUser Kommunizierender Benutzer.
     * @return Menge der Benutzer, mit denen der Benutzer kommunizieren kann.
     */
    public Map<UUID, User> getCommunicableUsers(User communicatingUser) {
        return communicationRegion.getCommunicableUsers(communicatingUser);
    }

    /**
     * Überprüft, ob in diesem Kontext mit einem übergebenen Medium kommuniziert werden kann.
     * @param medium Zu überprüfender Medium.
     * @return true, wenn mit dem Medium kommuniziert werden kann, sonst false.
     */
    public boolean canCommunicateWith(CommunicationMedium medium) {
        return communicationMedia.contains(medium);
    }

    /**
     * Gibt den übergeordneten Kontext zurück.
     * @return Übergeordneter Kontext.
     */
    public Context getParent() {
        return parent;
    }

    /**
     * Gibt die Menge aller untergeordneter Kontexte zurück.
     * @return Menge aller untergeordneter Kontexte.
     */
    public Map<ContextID, SpatialContext> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    /**
     * Gibt die Benutzer zurück, die sich gerade innerhalb dieses Kontexts befinden.
     * @return Menge aller Benutzer im Kontext.
     */
    public Map<UUID, User> getUsers() {
        return Collections.unmodifiableMap(containedUsers);
    }
}
