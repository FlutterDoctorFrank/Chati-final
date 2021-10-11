package model.context;

import controller.network.ClientSender.SendAction;
import model.communication.CommunicationMedium;
import model.context.global.GlobalContext;
import model.context.spatial.Area;
import model.database.Database;
import model.database.IContextDatabase;
import model.exception.ContextNotFoundException;
import model.role.Role;
import model.user.IUser;
import model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Eine Klasse, welche einen Kontext der Anwendung repräsentiert. Ein Kontext legt den
 * Umfang fest, in welchem Benutzer Benachrichtigungen empfangen können, Rollen haben
 * können, kommunizieren können und sonstige Benutzeraktionen durchführen können.
 */
public abstract class Context implements IContext {

    /** Dient der eindeutigen Identifikation eines Kontextes.*/
    protected final ContextID contextId;

    /** Der Name eines Kontextes. */
    protected final String contextName;

    /** Der übergeordnete Kontext. */
    protected final Context parent;

    /** Die Menge aller untergeordneter (räumlicher) Kontexte. */
    protected final Map<ContextID, Area> children;

    /** Die in diesem Kontext enthaltenen Benutzer. */
    protected final Map<UUID, User> containedUsers;

    /** Die in diesem Kontext gemeldeten Benutzer. */
    protected final Map<UUID, User> reportedUsers;

    /** Die in diesem Kontext stummgeschalteten Benutzer. */
    protected final Map<UUID, User> mutedUsers;

    /** Die in diesem Kontext gesperrten Benutzer. */
    protected final Map<UUID, User> bannedUsers;

    /** Ermöglicht Zugriff auf die Datenbank */
    protected final IContextDatabase database;

    /**
     * Erzeugt eine Instanz eines Kontextes.
     * @param contextName Name des Kontextes.
     * @param parent Übergeordneter Kontext.
     */
    protected Context(@NotNull final String contextName, @Nullable final Context parent) {
        if (parent == null) {
            this.contextId = new ContextID(contextName);
        } else {
            this.contextId = new ContextID(parent.getContextId().getId().concat(".").concat(contextName));
        }
        this.contextName = contextName;
        this.parent = parent;
        this.children = new HashMap<>();
        this.containedUsers = new ConcurrentHashMap<>();
        this.reportedUsers = new HashMap<>();
        this.mutedUsers = new HashMap<>();
        this.bannedUsers = new HashMap<>();
        this.database = Database.getContextDatabase();
    }

    @Override
    public @NotNull ContextID getContextId() {
        return contextId;
    }

    @Override
    public @NotNull String getContextName() {
        return contextName;
    }

    @Override
    public @NotNull Map<UUID, IUser> getIUsers() {
        return Collections.unmodifiableMap(containedUsers);
    }

    @Override
    public @NotNull Map<UUID, IUser> getReportedUsers() {
        return Collections.unmodifiableMap(reportedUsers);
    }

    @Override
    public @NotNull Map<UUID, IUser> getMutedUsers() {
        return Collections.unmodifiableMap(mutedUsers);
    }

    @Override
    public @NotNull Map<UUID, IUser> getBannedUsers() {
        return Collections.unmodifiableMap(bannedUsers);
    }

    /**
     * Gibt die Benutzer zurück, die sich gerade innerhalb dieses Kontexts befinden und sich nicht in den
     * Kind-Kontexten befinden.
     * @return Menge aller exklusiven Benutzer im Kontext.
     */
    public @NotNull Map<UUID, User> getExclusiveUsers() {
        final Map<UUID, User> users = new HashMap<>(containedUsers);
        this.getChildren().values().forEach(child -> users.keySet().removeAll(child.containedUsers.keySet()));
        return users;
    }

    /**
     * Gibt die Benutzer zurück, die sich gerade innerhalb dieses Kontexts befinden.
     * @return Menge aller Benutzer im Kontext.
     */
    public @NotNull Map<UUID, User> getUsers() {
        return new HashMap<>(containedUsers);
    }

    /**
     * Fügt einen untergeordneten räumlichen Kontext hinzu.
     * @param child Hinzuzufügender Kontext.
     */
    public void addChild(@NotNull final Area child) {
        children.put(child.getContextId(), child);
    }

    /**
     * Entfernt einen untergeordneten räumlichen Kontext.
     * @param child Zu entfernender Kontext.
     */
    public void removeChild(@NotNull final Area child) {
        child.stop();
        children.remove(child.getContextId());
    }

    /**
     * Fügt einen Benutzer in einen Kontext hinzu. Stellt sicher, dass dieser Benutzer auch in den übergeordneten
     * Kontexten hinzugefügt wird, sofern er dort nicht enthalten ist.
     * @param user Hinzuzufügender Benutzer.
     */
    public void addUser(@NotNull final User user) {
        if (!this.contains(user)) {
            this.containedUsers.put(user.getUserId(), user);

            if (this.parent != null && !this.parent.contains(user)) {
                this.parent.addUser(user);
            }

            System.out.println("User '" + user.getUsername() + "' added to context: " + this.contextId);
        }
    }

    /**
     * Entfernt einen Benutzer aus einem Kontext, wenn dieser enthalten ist. Stellt sicher, dass dieser Benutzer auch
     * aus allen untergeordneten Kontexten entfernt wird.
     * @param user Zu entfernender Benutzer.
     */
    public void removeUser(@NotNull final User user) {
        if (this.contains(user)) {
            this.containedUsers.remove(user.getUserId());
            System.out.println("User '" + user.getUsername() + "' removed from context: " + this.contextId);
            this.getChildren().values().forEach(children -> children.removeUser(user));
        }
    }

    /**
     * Fügt einen Benutzer zu den gemeldeten Benutzern in diesem Kontext hinzu. Stellt sicher, dass der Benutzer in
     * allen untergeordneten Kontexten als gemeldeter Benutzer hinzugefügt wird.
     * @param user Hinzuzufügender Benutzer.
     */
    public void addReportedUser(@NotNull final User user) {
        if (reportedUsers.containsKey(user.getUserId())) {
            return;
        }
        user.updateLastReported();

        reportedUsers.put(user.getUserId(), user);
        children.values().forEach(child -> child.addReportedUser(user));

        // Sende die Information nur, wenn es sich um eine Welt handelt.
        if (parent != null && parent.equals(GlobalContext.getInstance())) {
            // Die Information über den gemeldeten Benutzer ist nur für die Benutzer innerhalb der Welt relevant.
            containedUsers.values().forEach(receiver -> receiver.send(SendAction.USER_INFO, user));
        }
    }

    /**
     * Entfernt einen Benutzer aus den gemeldeten Benutzern in diesem Kontext. Stellt sicher, dass der Benutzer aus
     * allen untergeordneten Kontexten als gemeldeter Benutzer entfernt wird.
     * @param user Zu entfernender Benutzer.
     */
    public void removeReportedUser(@NotNull final User user) {
        if (!reportedUsers.containsKey(user.getUserId())) {
            return;
        }

        children.values().forEach(child -> child.removeReportedUser(user));
        reportedUsers.remove(user.getUserId());

        // Sende die Information nur, wenn es sich um eine Welt handelt.
        if (parent != null && parent.equals(GlobalContext.getInstance())) {
            // Die Information über den nun nicht mehr gemeldeten Benutzer ist nur für die Benutzer innerhalb der Welt relevant.
            containedUsers.values().forEach(receiver -> receiver.send(SendAction.USER_INFO, user));
        }
    }

    /**
     * Fügt einen Benutzer zu den stummgeschalteten Benutzern in diesem Kontext hinzu. Stellt sicher, dass der Benutzer
     * in allen untergeordneten Kontexten als stummgeschalteter Benutzer hinzugefügt wird.
     * @param user Hinzuzufügender Benutzer.
     */
    public void addMutedUser(@NotNull final User user) {
        if (mutedUsers.containsKey(user.getUserId())) {
            return;
        }

        mutedUsers.put(user.getUserId(), user);
        children.values().forEach(child -> child.addMutedUser(user));

        // Sende die Information nur, wenn es sich um den globalen Kontext, einer Welt oder dem obersten Bereich, in
        // dem der Benutzer stumm geschaltet wurde, handelt.
        if (parent == null || parent.equals(GlobalContext.getInstance()) || !parent.isMuted(user)) {
            // Die Information über den stumm geschalteten Benutzer ist für die Benutzer relevant die sich momentan in
            // diesem Kontext befinden. Würde diese Information hier nicht versendet werden, müssten alle Benutzer diesen
            // Kontext verlassen und neu betreten, um diese Information zu erhalten.
            containedUsers.values().forEach(receiver -> receiver.send(SendAction.CONTEXT_INFO, this));
        }
    }

    /**
     * Entfernt einen Benutzer aus den stummgeschalteten Benutzern in diesem Kontext. Stellt sicher, dass der Benutzer
     * in allen untergeordneten Kontexten als stummgeschalteter Benutzer entfernt wird.
     * @param user Zu entfernender Benutzer.
     */
    public void removeMutedUser(@NotNull final User user) {
        if (!mutedUsers.containsKey(user.getUserId())) {
            return;
        }

        children.values().forEach(child -> child.removeMutedUser(user));
        mutedUsers.remove(user.getUserId());

        // Sende die Information nur, wenn es sich um den globalen Kontext, einer Welt oder dem obersten Bereich, in
        // die Stummschaltung des Benutzers aufgehoben wurde, handelt.
        if (parent == null || parent.equals(GlobalContext.getInstance()) || !parent.isMuted(user)) {
            // Die Information über den nun nicht mehr stumm geschalteten Benutzer ist für die Benutzer relevant die sich
            // momentan in diesem Kontext befinden. Würde diese Information hier nicht versendet werden, müssten alle
            // Benutzer diesen Kontext verlassen und neu betreten, um diese Information zu erhalten.
            containedUsers.values().forEach(receiver -> receiver.send(SendAction.CONTEXT_INFO, this));
        }
    }

    /**
     * Wird von der Datenbank verwendet, um die initiale Menge von gesperrten Benutzern hinzuzufügen.
     * @param bannedUsers Hinzuzufügende Benutzer.
     */
    public void addBannedUsers(@NotNull final Map<UUID, User> bannedUsers) {
        this.bannedUsers.putAll(bannedUsers);
        children.values().forEach(child -> child.addBannedUsers(bannedUsers));
    }

    /**
     * Fügt einen Benutzer zu den gesperrten Benutzern in diesem Kontext hinzu. Stellt sicher, dass der Benutzer in
     * allen untergeordneten Kontexten als gesperrter Benutzer hinzugefügt wird.
     * @param user Hinzuzufügender Benutzer.
     */
    public void addBannedUser(@NotNull final User user) {
        if (bannedUsers.containsKey(user.getUserId())) {
            return;
        }

        bannedUsers.put(user.getUserId(), user);
        children.values().forEach(child -> child.addBannedUser(user));

        // Sende die Information nur, wenn es sich um eine Welt handelt.
        if (parent != null && parent.equals(GlobalContext.getInstance())) {
            if (!user.hasRole(this, Role.BOT)) {
                database.addBannedUser(user, this);
            }
            // Die Information über den gesperrten Benutzer ist nur für die Benutzer innerhalb der Welt relevant.
            containedUsers.values().forEach(receiver -> receiver.send(SendAction.USER_INFO, user));
        }
    }

    /**
     * Entfernt einen Benutzer aus den gesperrten Benutzern in diesem Kontext. Stellt sicher, dass der Benutzer aus
     * allen untergeordneten Kontexten als gemeldeter Benutzer entfernt wird.
     * @param user Zu entfernender Benutzer.
     */
    public void removeBannedUser(@NotNull final User user) {
        if (!bannedUsers.containsKey(user.getUserId())) {
            return;
        }

        children.values().forEach(child -> child.removeBannedUser(user));
        bannedUsers.remove(user.getUserId());

        // Sende die Information nur, wenn es sich um eine Welt handelt.
        if (parent != null && parent.equals(GlobalContext.getInstance())) {
            if (!user.hasRole(this, Role.BOT)) {
                database.removeBannedUser(user, this);
            }
            // Die Information über den nun nicht mehr gesperrten Benutzer ist nur für die Benutzer innerhalb der Welt relevant.
            containedUsers.values().forEach(receiver -> receiver.send(SendAction.USER_INFO, user));
        }
    }

    /**
     * Ermittelt alle Benutzer, mit denen ein übergebener Benutzer in diesem Kontext kommunizieren kann.
     * @param communicatingUser Kommunizierender Benutzer.
     * @return Menge der Benutzer, mit denen der Benutzer kommunizieren kann.
     */
    public abstract @NotNull Map<UUID, User> getCommunicableUsers(@NotNull final User communicatingUser);

    /**
     * Überprüft, ob in diesem Kontext mit einem übergebenen Medium kommuniziert werden kann.
     * @param medium Zu überprüfendes Medium.
     * @return true, wenn mit dem Medium kommuniziert werden kann, sonst false.
     */
    public abstract boolean canCommunicateWith(@NotNull final CommunicationMedium medium);

    /**
     * Überprüft, ob ein Benutzer in diesem Kontext enthalten ist.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer enthalten ist, sonst false.
     */
    public boolean contains(@NotNull final User user) {
        return containedUsers.containsKey(user.getUserId());
    }

    /**
     * Überprüft, ob ein Benutzer in diesem Kontext oder einem übergeordneten Kontext gemeldet ist.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer gemeldet ist, sonst false.
     */
    public boolean isReported(@NotNull final User user) {
        return reportedUsers.containsKey(user.getUserId()) || (parent != null && parent.isReported(user));
    }

    /**
     * Überprüft, ob ein Benutzer in diesem Kontext oder einem übergeordneten Kontext stummgeschaltet ist.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer stummgeschaltet ist, sonst false.
     */
    public boolean isMuted(@NotNull final User user) {
        return mutedUsers.containsKey(user.getUserId()) || (parent != null && parent.isMuted(user));
    }

    /**
     * Überprüft, ob ein Benutzer in diesem Kontext oder einem übergeordneten Kontext gesperrt ist.
     * @param user Zu überprüfender Benutzer.
     * @return true, wenn der Benutzer gesperrt ist, sonst false.
     */
    public boolean isBanned(@NotNull final User user) {
        return bannedUsers.containsKey(user.getUserId()) || (parent != null && parent.isBanned(user));
    }

    /**
     * Überprüft, ob dieser Kontext im übergebenen Kontext enthalten ist.
     * @param context Zu überprüfender Kontext.
     * @return true, wenn der übergebene Kontext übergeordnet ist, sonst false.
     */
    public boolean isInContext(@NotNull final Context context) {
        return equals(context) || (parent != null && parent.getChildren().containsKey(contextId)
                && parent.isInContext(context));
    }

    /**
     * Ermittelt den letzten gemeinsamen Vorfahren (= übergeordneten Kontext), dieses Kontextes und des übergebenen
     * Kontextes.
     * @param context Zu überprüfender Kontext.
     * @return Letzter gemeinsamer Vorfahre beider Kontexte.
     */
    public @NotNull Context lastCommonAncestor(@NotNull final Context context) {
        return isInContext(context) ? context : (context.isInContext(this) ? this :
                parent != null ? parent.lastCommonAncestor(context) : this);
    }

    /**
     * Gibt den übergeordneten Kontext zurück.
     * @return Übergeordneter Kontext.
     */
    public @Nullable Context getParent() {
        return parent;
    }

    /**
     * Gibt die Menge aller untergeordneter (räumlicher) Kontexte zurück.
     * @return Menge aller untergeordneter Kontexte.
     */
    public @NotNull Map<ContextID, Area> getChildren() {
        /*
         * Wenn es sich bei diesem Kontext um eine Welt handelt, wird eine Kopie der Kind-Kontexte zurückgegeben, da
         * die originale Map, während dem iterieren durch die zurückgegebene Map, durch das Entfernen von privaten
         * Räumen gleichzeitig modifiziert werden könnte.
         */
        if (parent != null && parent.equals(GlobalContext.getInstance())) {
            return Map.copyOf(children);
        }
        return Collections.unmodifiableMap(children);
    }

    /**
     * Gibt einen untergeordneten Kontext zu gegebener ID zurück.
     * @param contextId ID des gesuchten Kontextes.
     * @return Kontext mit der übergebenen ID.
     */
    public @NotNull Context getContext(ContextID contextId) throws ContextNotFoundException {
        Context found = null;
        if (this.contextId.equals(contextId)) {
            return this;
        } else {
            for (Area child : children.values()) {
                try {
                    found = child.getContext(contextId);
                    break;
                } catch (ContextNotFoundException ignored) {
                }
            }
        }
        if (found == null) {
            throw new ContextNotFoundException("There is no context with this ID.", contextId);
        }
        return found;
    }

    /**
     * Stoppt alle Threads, die in diesem, oder untergeordneten Kontexten gestartet wurden.
     */
    public void stop() {
        children.values().forEach(Context::stop);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Context context = (Context) o;
        return contextId.equals(context.contextId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextId);
    }
}