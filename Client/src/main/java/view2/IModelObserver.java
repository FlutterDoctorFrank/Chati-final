package view2;

public interface IModelObserver {
    void setUserInfoChanged();
    void setUserNotificationChanged();
    void setUserPositionChanged();
    void setWorldChanged();
    void setRoomChanged();
    void setMusicChanged();
}
