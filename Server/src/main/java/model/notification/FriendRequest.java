package model.notification;

import controller.network.ClientSender.SendAction;
import model.MessageBundle;
import model.communication.message.TextMessage;
import model.context.global.GlobalContext;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;

/** Eine Klasse, welche Freundschaftsanfragen repräsentiert. */
public class FriendRequest extends Notification {

    /** Benutzer, der die Freundschaftsanfrage stellt. */
    private final User requestingUser;

    /**
     * Erzeugt eine neue Instanz der Freundschaftsanfrage.
     * @param owner Der Eigentümer dieser Benachrichtigung.
     * @param userMessage Die vom anfragenden Benutzer beigefügte Nachricht.
     * @param requestingUser Der anfragende Benutzer.
     */
    public FriendRequest(@NotNull final User owner, @NotNull final String userMessage, @NotNull final User requestingUser) {
        super(NotificationType.FRIEND_REQUEST, owner, GlobalContext.getInstance(),
                new MessageBundle("friendRequestKey", requestingUser.getUsername(), userMessage));
        this.requestingUser = requestingUser;
    }

    @Override
    public void accept() {
        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer existiert nicht mehr");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob die Benutzer bereits befreundet sind.
        if (owner.isFriend(requestingUser) && requestingUser.isFriend(owner)) {
            MessageBundle messageBundle = new MessageBundle("Du bist bereits mit diesem Benutzer befreundet.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der anfragende Benutzer den Eigentümer der Benachrichtigung ignoriert.
        if (requestingUser.isIgnoring(owner)) {
            MessageBundle messageBundle = new MessageBundle("Eine Freundschaft mit diesem Benutzer ist nicht möglich.");
            TextMessage infoMessage = new TextMessage(messageBundle);
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
                new MessageBundle("messageKey"));
        requestingUser.addNotification(acceptNotification);
    }

    @Override
    public void decline() {
        // Überprüfe, ob der anfragende Benutzer noch existiert.
        if (!UserAccountManager.getInstance().isRegistered(requestingUser.getUserId())) {
            MessageBundle messageBundle = new MessageBundle("Der anfragende Benutzer existiert nicht mehr");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob die Benutzer bereits befreundet sind.
        if (owner.isFriend(requestingUser) || requestingUser.isFriend(owner)) {
            MessageBundle messageBundle = new MessageBundle("Du bist bereits mit diesem Benutzer befreundet.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            owner.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        // Benachrichtige den anfragenden Benutzer über die Ablehnung der Freundschaftsanfrage.
        Notification declineNotification = new Notification(requestingUser, GlobalContext.getInstance(),
                new MessageBundle("messageKey"));
        requestingUser.addNotification(declineNotification);
    }
}