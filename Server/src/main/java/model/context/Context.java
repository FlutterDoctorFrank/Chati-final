package model.context;

import controller.network.ClientSender;
import model.communication.CommunicationMedium;
import model.communication.CommunicationRegion;
import model.context.spatial.Music;
import model.context.spatial.SpatialContext;
import model.user.IUser;
import model.user.User;

import java.util.*;

public class Context implements IContext {
    protected final ContextID contextID;
    protected final String contextName;
    protected final Context parent;
    protected final Map<ContextID, SpatialContext> children;
    private final Map<UUID, User> containedUsers;
    private final Map<UUID, User> reportedUsers;
    private final Map<UUID, User> mutedUsers;
    private final Map<UUID, User> bannedUsers;
    private CommunicationRegion communicationRegion;
    private Set<CommunicationMedium> communicationMedia;
    private Music music;

    protected Context(String contextName, Context parent) {
        this.contextID = new ContextID(parent.getContextID().getId().concat(contextName));
        this.contextName = contextName;
        this.parent = parent;
        this.children = new HashMap<>();
        this.containedUsers = new HashMap<>();
        this.reportedUsers = new HashMap<>();
        this.mutedUsers = new HashMap<>();
        this.bannedUsers = new HashMap<>();
        this.communicationRegion = null;
        this.communicationMedia = null;
        this.music = null;
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
        containedUsers.put(user.getUserId(), user);
        if (parent != null && !parent.contains(user)) {
            parent.addUser(user);
        }

        // send new User music information
        user.getClientSender().send(ClientSender.SendAction.CONTEXT_MUSIC, this);
    }

    public void removeUser(User user) {
        containedUsers.remove(user.getUserId());
        children.forEach((childID, child) -> {
            if (child.contains(user)) {
                child.removeUser(user);
            }
        });
    }

    public void addReportedUser(User user) {
        reportedUsers.put(user.getUserId(), user);
        user.updateUserInfo();
    }

    public void removeReportedUser(User user) {
        reportedUsers.remove(user.getUserId());
        children.forEach((childID, child) -> {
            if (child.isReported(user)) {
                child.removeReportedUser(user);
            }
        });
        user.updateUserInfo();
    }

    public void addMutedUser(User user) {
        mutedUsers.put(user.getUserId(), user);
        user.updateUserInfo();
    }

    public void removeMutedUser(User user) {
        mutedUsers.remove(user.getUserId());
        children.forEach((childID, child) -> {
            if (child.isMuted(user)) {
                child.removeMutedUser(user);
            }
        });
        user.updateUserInfo();
    }

    public void addBannedUser(User user) {
        bannedUsers.put(user.getUserId(), user);
        user.updateUserInfo();
    }

    public void removeBannedUser(User user) {
        bannedUsers.remove(user.getUserId());
        children.forEach((childID, child) -> {
            if (child.isBanned(user)) {
                child.removeBannedUser(user);
            }
        });
        user.updateUserInfo();
    }

    public boolean contains(User user) {
        return containedUsers.containsKey(user.getUserId());
    }

    public boolean isReported(User user) {
        return reportedUsers.containsKey(user.getUserId());
    }

    public boolean isMuted(User user) {
        return mutedUsers.containsKey(user.getUserId()) || parent.isMuted(user);
    }

    public boolean isBanned(User user) {
        return bannedUsers.containsKey(user.getUserId()) || parent.isBanned(user);
    }

    public boolean isInContext(Context context) {
        return equals(context) || parent.isInContext(context);
    }

    public Context lastCommonAncestor(Context context) {
        if (isInContext(context)) {
            return context;
        } else if (context.isInContext(this)) {
            return this;
        } else {
            return parent.lastCommonAncestor(context);
        }
    }

    public Context getParent() {
        return parent;
    }

    public Map<ContextID, SpatialContext> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    public void playMusic(Music music) {
        this.music = music;

        // Send music information to all users in context
        containedUsers.forEach((userID, user) -> {
            user.getClientSender().send(ClientSender.SendAction.CONTEXT_MUSIC, this);
        });
    }

    public void stopMusic() {
        this.music = null;

        // Send music information to all users in context
        containedUsers.forEach((userID, user) -> {
            user.getClientSender().send(ClientSender.SendAction.CONTEXT_MUSIC, this);
        });
    }

    public java.util.Map<UUID, User> getCommunicableUsers(User communicatingUser) {
        return communicationRegion.getCommunicableUsers(communicatingUser);
    }

    public boolean canCommunicateWith(CommunicationMedium medium) {
        return communicationMedia.contains(medium);
    }

    public Map<UUID, User> getContainedUsers() {
        return containedUsers;
    }

    public void addChild(SpatialContext child) {
        children.put(child.getContextID(), child);
    }
}
