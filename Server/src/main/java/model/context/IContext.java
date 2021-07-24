package model.context;

import model.context.spatial.Music;
import model.user.IUser;

import java.util.Map;
import java.util.UUID;

public interface IContext {
    public ContextID getContextID();
    public String getContextName();
    public Map<UUID, IUser> getIUsers();
    public Music getMusic();
}
