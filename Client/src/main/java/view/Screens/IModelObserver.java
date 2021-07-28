package view.Screens;

public interface IModelObserver {
    void setUserInfoChanged();
    void setUserNotificationChanged();
    void setUserPositionChanged();
    void setWorldInfoChanged();
    void setRoomInfoChanged();
    void setMapChanged();
}
