package view.Screens;

public interface IModelObserver {
    void setUserInfoChanged();
    void setUserNotificationChanged();
    void setUserPositionChanged();
    void setWorldChanged();
    void setRoomChanged();
}
