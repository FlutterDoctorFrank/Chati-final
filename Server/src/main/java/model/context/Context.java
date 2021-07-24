package model.context;

import model.context.spatial.Music;
import model.context.spatial.SpatialContext;
import model.user.IUser;
import model.user.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Context implements IContext {
    private ContextID contextID;
    private String contextName;
    private Context parent;
    private Map<ContextID, SpatialContext> children;
    private Map<UUID, User> containedUsers;
    private Map<UUID, User> reportedUsers;
    private Map<UUID, User> mutedUsers;
    private Map<UUID, User> bannedUsers;
    private Music music;

    protected Context(String contextName, Context parent, Map<ContextID, SpatialContext> children) {
        // generate contextID
        this.contextName = contextName;
        this.parent = parent;
        this.children = children;
        this.containedUsers = new HashMap<>();
        this.reportedUsers = new HashMap<>();
        this.mutedUsers = new HashMap<>();
        this.bannedUsers = new HashMap<>();
    }

    public Context() {

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

    public Map<UUID, User> getUsers() {
        return Collections.unmodifiableMap(containedUsers);
    }

    @Override
    public Music getMusic() {
        return music;
    }

    public void addUser(User user) {
        containedUsers.put(user.getUserID(), user);
        if (parent != null && !parent.contains(user)) {
            parent.addUser(user);
        }
    }

    public void removeUser(User user) {
        containedUsers.remove(user.getUserID());
        children.forEach((childID, child) -> {
            if (child.contains(user)) {
                child.removeUser(user);
            }
        });
    }

    public void addReportedUser(User user) {
        reportedUsers.put(user.getUserID(), user);
    }

    public void removeReportedUser(User user) {
        reportedUsers.remove(user.getUserID());
    }

    public void addMutedUser(User user) {
        mutedUsers.put(user.getUserID(), user);
    }

    public void removeMutedUser(User user) {
        mutedUsers.remove(user.getUserID());
    }

    public void addBannedUser(User user) {
        bannedUsers.put(user.getUserID(), user);
    }

    public void removeBannedUser(User user) {
        bannedUsers.remove(user.getUserID());
    }

    public boolean contains(User user) {
        return containedUsers.containsKey(user.getUserID());
    }

    public boolean isReported(User user) {
        return reportedUsers.containsKey(user.getUserID());
    }

    public boolean isMuted(User user) {
        return mutedUsers.containsKey(user.getUserID()) || parent.isMuted(user);
    }

    public boolean isBanned(User user) {
        return bannedUsers.containsKey(user.getUserID()) || parent.isBanned(user);
    }

    public boolean isInContext(Context context) {
        return equals(context) || parent.isInContext(context);
    }

    public Context lastCommonAncestor(Context context) {
        return context.isInContext(this) ? this : parent.lastCommonAncestor(context);
    }

    public Context getParent() {
        return parent;
    }

    public Map<ContextID, SpatialContext> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    public void playMusic(Music music) {
        this.music = music;
    }

    public void stopMusic() {
        this.music = null;
    }

    public Map<UUID, User> getContainedUsers() {
        return containedUsers;
    }
}
