package view;

/**
 * Ein Interface, über welche das Modell die View über anzuzeigende Änderungen benachrichtigen kann.
 */
public interface IModelObserver {

    /**
     * Benachrichtigt die View darüber, dass sich Informationen von Benutzern geändert haben.
     */
    void setUserInfoChanged();

    /**
     * Benachrichtigt die View darüber, dass neue Benachrichtigungen empfangen wurden.
     */
    void setNewNotificationReceived();

    /**
     * Benachrichtigt die View darüber, dass sich Informationen von vorhandenen Benachrichtigungen geändert haben.
     */
    void setUserNotificationChanged();

    /**
     * Benachrichtigt die View darüber, dass gelesene Benachrichtigungen empfangen wurden.
     */
    void setUserNotificationReceived();

    /**
     * Benachrichtigt die View darüber, dass sich die Menge der Welten geändert hat.
     */
    void setWorldListChanged();

    /**
     * Benachrichtigt die View darüber, dass sich die Menge der privaten Räume in der Welt des angemeldeten Benutzers
     * geändert hat.
     */
    void setRoomListChanged();

    /**
     * Benachrichtigt die View darüber, dass sich die Welt geändert hat, in der sich der angemeldete Benutzer befindet.
     */
    void setWorldChanged();

    /**
     * Benachrichtigt die View darüber, dass sich der Raum geändert hat, in dem sich der angemeldete Benutzer befindet.
     */
    void setRoomChanged();

    /**
     * Benachrichtigt die View darüber, dass sich die gerade abgespielte Musik geändert hat.
     */
    void setMusicChanged();
}
