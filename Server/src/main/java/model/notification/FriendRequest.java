package model.notification;

import controller.network.ClientSender.SendAction;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.global.GlobalContext;
import model.exception.IllegalNotificationActionException;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/** Eine Klasse, welche Freundschaftsanfragen repräsentiert. */
public class FriendRequest extends Notification {

    /** Benutzer, der die Freundschaftsanfrage stellt. */
    private final User requestingUser;
    private final String userMessage;

    /**
     * Erzeugt eine neue Instanz der Freundschaftsanfrage.
     * @param owner Der Eigentümer dieser Benachrichtigung.
     * @param userMessage Die vom anfragenden Benutzer beigefügte Nachricht.
     * @param requestingUser Der anfragende Benutzer.
     */
    public FriendRequest(@NotNull final User owner, @NotNull final String userMessage, @NotNull final User requestingUser) {
        super(NotificationType.FRIEND_REQUEST, owner, GlobalContext.getInstance(),
                new MessageBundle("request.friend.notification", requestingUser.getUsername(), userMessage));
        this.requestingUser = requestingUser;
        this.userMessage = userMessage;
    }

    public FriendRequest(@NotNull final UUID notificationId, @NotNull final LocalDateTime timestamp,
                         @NotNull final User owner, @NotNull final User requester, @NotNull final String message,
                         final boolean read, final boolean accepted, final boolean declined) {
        super(notificationId, NotificationType.FRIEND_REQUEST, GlobalContext.getInstance(), owner, timestamp,
                new MessageBundle("request.friend.notification", requester.getUsername(), message),
                read, accepted, declined);

        this.requestingUser = requester;
        this.userMessage = message;
    }

    public @NotNull User getRequestingUser() {
        return this.requestingUser;
    }

    public @NotNull String getUserMessage() {
        return this.userMessage;
    }

    @Override
    public void accept() throws IllegalNotificationActionException {
        super.accept();

        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            TextMessage infoMessage = new TextMessage("request.friend.user-not-found", requestingUser.getUsername());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        // Überprüfe, ob die Benutzer bereits befreundet sind.
        if (owner.isFriend(requestingUser) && requestingUser.isFriend(owner)) {
            TextMessage infoMessage = new TextMessage("request.friend.already-friends", requestingUser.getUsername());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der anfragende Benutzer den Eigentümer der Benachrichtigung ignoriert.
        if (requestingUser.isIgnoring(owner)) {
            TextMessage infoMessage = new TextMessage("request.friend.not-possible", requestingUser.getUsername());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der Eigentümer der Benachrichtigung den anfragenden Benutzer ignoriert.
        if (owner.isIgnoring(requestingUser)) {
            owner.unignoreUser(requestingUser);
        }
        // Füge die Benutzer jeweils als Freund hinzu.
        owner.addFriend(requestingUser);
        requestingUser.addFriend(owner);
        // Benachrichtige den anfragenden Benutzer über die Annahme der Freundschaftsanfrage.
        Notification acceptNotification = new Notification(requestingUser, GlobalContext.getInstance(),
                new MessageBundle("request.friend.accepted", owner.getUsername()));
        requestingUser.addNotification(acceptNotification);
    }

    @Override
    public void decline() throws IllegalNotificationActionException {
        super.decline();

        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            TextMessage infoMessage = new TextMessage("request.friend.user-not-found", requestingUser.getUsername());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob die Benutzer bereits befreundet sind.
        if (owner.isFriend(requestingUser) || requestingUser.isFriend(owner)) {
            TextMessage infoMessage = new TextMessage("request.friend.already-friends", requestingUser.getUsername());
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        // Benachrichtige den anfragenden Benutzer über die Ablehnung der Freundschaftsanfrage.
        Notification declineNotification = new Notification(requestingUser, GlobalContext.getInstance(),
                new MessageBundle("request.friend.declined", owner.getUsername()));
        requestingUser.addNotification(declineNotification);
    }
}