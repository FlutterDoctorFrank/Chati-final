package model.communication;

import controller.network.ClientSender.SendAction;
import model.communication.message.MessageType;
import model.communication.message.TextMessage;
import model.communication.message.VoiceMessage;
import model.context.Context;
import model.context.spatial.Area;
import model.context.spatial.Room;
import model.context.spatial.World;
import model.exception.UserNotFoundException;
import model.role.Permission;
import model.user.Status;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.UUID;

public class CommunicationHandler {

    /** Ein regulärer Ausdruck für das Verwenden eines Chatbefehls in einer Nachricht.*/
    private static final String CHAT_COMMAND = "\\\\.*";

    private CommunicationHandler() {
    }

    /**
     * Ermittelt die Benutzer, die sehen sollen dass ein Benutzer gerade tippt und leitet diese Information weiter.
     * @param typingUser Tippender Benutzer.
     */
    public static void handleTyping(@NotNull final User typingUser) {
        if (typingUser.getLocation() == null) {
            throw new IllegalStateException("Communicators location is not available");
        }

        // Überprüfung, ob in dem Bereich des Benutzers Textnachrichten versendet werden können oder der sendende
        // Benutzer in dem Kontext stummgeschaltet ist.
        Area communicationContext = typingUser.getLocation().getArea();
        if (!communicationContext.canCommunicateWith(CommunicationMedium.VOICE)
                || communicationContext.isMuted(typingUser)) {
            return;
        }

        // Ermittle die empfangsberechtigten Benutzer gemäß der Kommunikationsform, ohne den sendenden Benutzer.
        Map<UUID, User> receivers = typingUser.getCommunicableUsers();
        receivers.remove(typingUser.getUserId());

        // Versende die Information.
        receivers.values().forEach(user -> user.send(SendAction.TYPING, typingUser));
    }

    /**
     * Ermittelt den Nachrichtentyp der Nachricht durch ein am Anfang eingegebenes Muster, sowie die Nutzer, die diese
     * Nachricht empfangen sollen und leitet sie an diese weiter. Existiert kein Nachrichtentyp für das eingegebene
     * Muster, oder hat der Anwender nicht die Berechtigung diesen zu benutzen, wird er mit einer Textnachricht
     * darüber informiert.
     * @param sender Sender der Nachricht.
     * @param receivedMessage Erhaltene Nachricht.
     * @see TextMessage
     * @see MessageType
     */
    public static void handleTextMessage(@NotNull final User sender, @NotNull final String receivedMessage) {
        if (receivedMessage.matches(CHAT_COMMAND)) {
            String commandMessage = receivedMessage.substring(1);

            for (ChatCommand command : ChatCommand.values()) {
                if (commandMessage.matches(command.commandPattern)) {
                    command.handle(sender, commandMessage);
                    return;
                }
            }
            // Informiere den kommunizierenden Benutzer darüber, dass er versucht hat, einen nicht existierenden
            // Chatbefehl zu verwenden.
            TextMessage infoMessage = new TextMessage("chat.command.not-found", commandMessage.split("\\s")[0]);
            sender.send(SendAction.MESSAGE, infoMessage);
        } else {
            handleStandardMessage(sender, receivedMessage);
        }
    }

    /**
     * Ermittelt die Nutzer, die diese Nachricht gemäß der im räumlichen Kontext des Senders geltenden
     * Kommunikationsform empfangen sollen und leitet sie an diese weiter.
     * @param sender Kommunizierender Benutzer.
     * @param message Zu versendende Nachricht.
     * @see MessageType#STANDARD
     */
    private static void handleStandardMessage(@NotNull final User sender, @NotNull final String message) {
        if (sender.getLocation() == null) {
            throw new IllegalStateException("Communicators location is not available");
        }

        // Überprüfe, ob in dem Bereich des Benutzers Textnachrichten versendet werden können.
        Area communicationContext = sender.getLocation().getArea();
        if (!communicationContext.canCommunicateWith(CommunicationMedium.TEXT)) {
            TextMessage infoMessage = new TextMessage("chat.standard.not-possible", communicationContext.getContextName());
            sender.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        // Überprüfe, ob der sendende Benutzer in dem Kontext stummgeschaltet ist.
        if (communicationContext.isMuted(sender)) {
            // Informiere den kommunizierenden Benutzer darüber, dass er in dem Kontext stummgeschaltet ist.
            TextMessage infoMessage = new TextMessage("chat.standard.muted", communicationContext.getContextName());
            sender.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        // Ermittle die empfangsberechtigten Benutzer gemäß der Kommunikationsform, einschließlich dem sendenden
        // Benutzer.
        Map<UUID, User> receivers = sender.getCommunicableUsers();

        // Versende die Textnachricht.
        TextMessage textMessage = new TextMessage(sender, message, MessageType.STANDARD);
        receivers.values().forEach(user -> user.send(SendAction.MESSAGE, textMessage));
    }

    /**
     * Ermittelt die Nutzer, die diese Nachricht gemäß der im räumlichen Kontext des Senders geltenden
     * Kommunikationsform empfangen sollen und leitet sie an diese weiter.
     * @param sender Kommunizierender Benutzer.
     * @param voiceData Zu versendende Nachricht.
     * @see VoiceMessage
     */
    public static void handleVoiceMessage(@NotNull final User sender, final byte[] voiceData) {
        if (sender.getLocation() == null) {
            throw new IllegalStateException("Communicators location is not available.");
        }

        // Überprüfung, ob in dem Bereich des Benutzers Sprachnachrichten versendet werden können oder der sendende
        // Benutzer in dem Kontext stummgeschaltet ist.
        Area communicationContext = sender.getLocation().getArea();
        if (!communicationContext.canCommunicateWith(CommunicationMedium.VOICE)
                || communicationContext.isMuted(sender)) {
            return;
        }

        // Ermittle die empfangsberechtigten Benutzer gemäß der Kommunikationsform, ohne den sendenden Benutzer.
        Map<UUID, User> receivers = sender.getCommunicableUsers();
        receivers.remove(sender.getUserId());

        // Versende die Sprachnachricht.
        VoiceMessage voiceMessage = new VoiceMessage(sender, voiceData);
        receivers.values().forEach(user -> user.send(SendAction.VOICE, voiceMessage));
    }

    /**
     * Filtert aus einer Menge von Benutzern die Benutzer heraus, die der kommunizierende Benutzer ignoriert und die
     * ihn ignorieren
     * @param sender Kommunizierender Benutzer.
     * @param users Die Menge von Benutzern, die gefiltert werden soll.
     */
    public static void filterIgnoredUsers(@NotNull final User sender, @NotNull final Map<UUID, User> users) {
        users.values().removeIf(user -> sender.isIgnoring(user) || user.isIgnoring(sender));
    }

    private enum ChatCommand {

        /**
         * Wird zur Verarbeitung einer Flüsternachricht verwendet. Überprüft, ob diese Flüsternachricht versendet werden
         * kann und versendet diese.
         * @see MessageType#WHISPER
         */
        WHISPER_MESSAGE_COMMAND("\\\\[a-zA-Z0-9]+\\s.*") {
            @Override
            protected void handle(@NotNull final User sender, @NotNull final String message) {
                // Extrahiere den Benutzernamen aus dem Befehl der Flüsternachricht.
                String[] usernameMessage = message.substring(1).split("\\s", 2);
                String username = usernameMessage[0];
                String sendMessage = usernameMessage[1];
                // Verarbeite die Flüsternachricht.
                if (sender.getLocation() == null) {
                    throw new IllegalStateException("Communicators location is not available");
                }

                // Überprüfe, ob ein Benutzer mit dem angegebenen Benutzernamen existiert.
                User receiver;
                try {
                    receiver = UserAccountManager.getInstance().getUser(username);

                    if (receiver.getLocation() == null) {
                        throw new IllegalStateException("Receivers location is not available");
                    }
                } catch (UserNotFoundException ex) {
                    // Informiere den kommunizierenden Benutzer darüber, dass kein Benutzer mit dem Namen existiert.
                    TextMessage infoMessage = new TextMessage("chat.command.whisper.user-not-found", username);
                    sender.send(SendAction.MESSAGE, infoMessage);
                    return;
                }

                // Überprüfe, ob der Benutzer mit sich selbst kommunizieren möchte.
                if (sender.equals(receiver)) {
                    TextMessage infoMessage = new TextMessage("chat.command.whisper.illegal-user");
                    sender.send(SendAction.MESSAGE, infoMessage);
                    return;
                }

                Area communicationContext = sender.getLocation().getArea();
                Map<UUID, User> communicableUsers = communicationContext.getCommunicableUsers(sender);

                // Überprüfe, ob die Benutzer gemäß der Kommunikationsform kommunizieren können, ob sich beide Benutzer
                // innerhalb eines Kontextes befinden, in dem einer der beiden Benutzer die Berechtigung zum Kontaktieren
                // anderer Benutzer hat, ob einer der beiden Benutzer den jeweils anderen ignoriert oder der Benutzer,
                // an den die Nachricht gerichtet ist, beschäftigt ist.
                Context commonContext = communicationContext.lastCommonAncestor(receiver.getLocation().getArea());

                if ((((!communicableUsers.containsKey(receiver.getUserId())
                        && !receiver.hasPermission(commonContext, Permission.CONTACT_USER))
                        || receiver.getStatus() == Status.BUSY)
                        && !sender.hasPermission(commonContext, Permission.CONTACT_USER))
                        || sender.isIgnoring(receiver) || receiver.isIgnoring(sender)) {
                    // Informiere den kommunizierenden Benutzer darüber, dass die Flüsterkommunikation nicht möglich ist.
                    TextMessage infoMessage = new TextMessage("chat.command.whisper.not-possible", receiver.getUsername());
                    sender.send(SendAction.MESSAGE, infoMessage);
                    return;
                }

                // Versende die Textnachricht an den Benutzer.
                TextMessage textMessage = new TextMessage(sender, sendMessage, MessageType.WHISPER);
                sender.send(SendAction.MESSAGE, textMessage);
                receiver.send(SendAction.MESSAGE, textMessage);
            }
        },

        /**
         * Wird zur Verarbeitung einer Raumnachricht verwendet. Überprüft, ob diese Raumnachricht versendet werden
         * kann und versendet diese.
         * @see MessageType#ROOM
         */
        ROOM_MESSAGE_COMMAND("(?i)room\\s.*") {
            @Override
            protected void handle(@NotNull final User sender, @NotNull final String message) {
                String sendMessage = message.split("\\s", 2)[1];

                if (sender.getLocation() == null) {
                    throw new IllegalStateException("Users location is not available");
                }

                // Überprüfe, ob sich der kommunizierende Benutzer in einem Raum befindet, für den er die nötige Berechtigung
                // besitzt.
                Room communicationRoom = sender.getLocation().getRoom();
                if (!sender.hasPermission(communicationRoom, Permission.CONTACT_CONTEXT)) {
                    // Informiere den kommunizierenden Benutzer darüber, dass er nicht die Berechtigung besitzt, um diesen
                    // Chatbefehl zu verwenden.
                    TextMessage infoMessage = new TextMessage("chat.command.room.not-permitted", communicationRoom.getContextName(),
                            Permission.CONTACT_CONTEXT);
                    sender.send(SendAction.MESSAGE, infoMessage);
                    return;
                }

                // Überprüfe, ob der kommunizierende Benutzer in dem Raum oder einem übergeordneten Kontext stummgeschaltet ist.
                if (communicationRoom.isMuted(sender)) {
                    // Informiere den kommunizierenden Benutzer darüber, dass er in dem Kontext stummgeschaltet ist.
                    TextMessage infoMessage = new TextMessage("chat.standard.muted", communicationRoom.getContextName());
                    sender.send(SendAction.MESSAGE, infoMessage);
                    return;
                }

                // Ermittle die empfangsberechtigten Benutzer.
                Map<UUID, User> receivers = communicationRoom.getUsers();
                filterIgnoredUsers(sender, receivers);

                // Versende die Textnachricht.
                TextMessage textMessage = new TextMessage(sender, sendMessage, MessageType.ROOM);
                receivers.values().forEach(user -> user.send(SendAction.MESSAGE, textMessage));
            }
        },

        /**
         * Wird zur Verarbeitung einer Weltnachricht verwendet. Überprüft, ob diese Weltnachricht versendet werden
         * kann und versendet diese.
         * @see MessageType#WORLD
         */
        WORLD_MESSAGE_COMMAND("(?i)world\\s.*"){
            @Override
            protected void handle(@NotNull final User sender, @NotNull final String message) {
                String sendMessage = message.split("\\s", 2)[1];

                if (sender.getLocation() == null) {
                    throw new IllegalStateException("Users location is not available");
                }
                // Überprüfe, ob sich der kommunizierende Benutzer in einer Welt befindet, für die er die nötige Berechtigung
                // besitzt.
                World communicationWorld = sender.getWorld();

                if (communicationWorld == null) {
                    throw new IllegalStateException("Users world is not available");
                }

                if (!sender.hasPermission(communicationWorld, Permission.CONTACT_CONTEXT)) {
                    // Informiere den kommunizierenden Benutzer darüber, dass er nicht die Berechtigung besitzt, um diesen
                    // Chatbefehl zu verwenden.
                    TextMessage infoMessage = new TextMessage("chat.command.world.not-permitted",
                            communicationWorld.getContextName(), Permission.CONTACT_CONTEXT);
                    sender.send(SendAction.MESSAGE, infoMessage);
                    return;
                }

                // Überprüfe, ob der kommunizierende Benutzer in dem Raum oder einem übergeordneten Kontext stummgeschaltet ist.
                if (communicationWorld.isMuted(sender)) {
                    // Informiere den kommunizierenden Benutzer darüber, dass er in dem Kontext stummgeschaltet ist.
                    TextMessage infoMessage = new TextMessage("chat.standard.muted", communicationWorld.getContextName());
                    sender.send(SendAction.MESSAGE, infoMessage);
                    return;
                }

                // Ermittle die empfangsberechtigten Benutzer.
                Map<UUID, User> receivers = communicationWorld.getUsers();
                filterIgnoredUsers(sender, receivers);

                // Versende die Textnachricht.
                TextMessage textMessage = new TextMessage(sender, sendMessage, MessageType.WORLD);
                receivers.values().forEach(user -> user.send(SendAction.MESSAGE, textMessage));
            }
        };

        /** Ein regulärer Ausdruck eines spezifischen Chatbefehls. */
        private final String commandPattern;

        /**
         * Verarbeitet die Nachricht gemäß dem Chatbefehl.
         * @param sender Sender der Nachricht.
         * @param message Erhaltene Nachricht.
         */
        protected abstract void handle(@NotNull final User sender, @NotNull final String message);

        /**
         * Erzeugt eine Instanz eines Chatbefehls.
         * @param commandPattern Pattern der Nachricht zur Verwendung dieses Chatbefehls.
         */
        ChatCommand(@NotNull final String commandPattern) {
            this.commandPattern = commandPattern;
        }
    }
}
