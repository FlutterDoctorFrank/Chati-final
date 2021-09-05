package view2;

public interface IModelObserver {
    void setUserInfoChanged();
    void setUserNotificationChanged();
    void setNewNotificationReceived();
    void setWorldChanged();
    void setRoomChanged();
    void setMusicChanged();
}
