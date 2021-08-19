package model.communication;

import controller.network.ClientSender;
import model.MessageBundle;
import model.communication.message.MessageType;
import model.communication.message.TextMessage;
import model.communication.message.VoiceMessage;
import model.context.Context;
import model.context.spatial.Area;
import model.context.spatial.Room;
import model.context.spatial.World;
import model.exception.UserNotFoundException;
import model.role.Permission;
import model.user.User;
import model.user.account.UserAccountManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class CommunicationHandler {

    /** Ein reguläre Ausdruck für das Verwenden eines Chatbefehls in einer Nachricht.*/
    private static final String CHAT_COMMAND = "\\\\.*";

    private CommunicationHandler() {
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
    public static void handleTextMessage(User sender, String receivedMessage) {
        if (receivedMessage.matches(CHAT_COMMAND)) {
            for (ChatCommand command : ChatCommand.values()) {
                if (receivedMessage.matches(command.commandPattern)) {
                    command.handle(sender, receivedMessage);
                    return;
                }
            }
            // Informiere den kommunizierenden Benutzer darüber, dass er versucht hat, einen nicht existierenden
            // Chatbefehl zu verwenden.
            MessageBundle messageBundle = new MessageBundle("Dieser Chatbefehl existiert nicht.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            sender.send(ClientSender.SendAction.MESSAGE, infoMessage);
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
    private static void handleStandardMessage(@NotNull User sender, @NotNull final String message) {
        if (sender.getLocation() == null) {
            throw new IllegalStateException("Communicators location is not available");
        }

        // Überprüfe, ob in dem Bereich des Benutzers Textnachrichten versendet werden können.
        Area communicationContext = sender.getLocation().getArea();
        if (!communicationContext.canCommunicateWith(CommunicationMedium.TEXT)) {
            MessageBundle messageBundle = new MessageBundle("In diesem Bereich ist keine Kommunikation in Schriftform möglich.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            sender.send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }

        // Überprüfe, ob der sendende Benutzer in dem Kontext stummgeschaltet ist.
        if (communicationContext.isMuted(sender)) {
            // Informiere den kommunizierenden Benutzer darüber, dass er in dem Kontext stummgeschaltet ist.
            MessageBundle messageBundle = new MessageBundle("Du bist in diesem Bereich stummgeschaltet.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            sender.send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }

        // Ermittle die empfangsberechtigten Benutzer gemäß der Kommunikationsform, einschließlich dem sendenden
        // Benutzer.
        Map<UUID, User> receivers = communicationContext.getCommunicableUsers(sender);
        filterIgnoredUsers(sender, receivers);

        // Versende die Textnachricht.
        TextMessage textMessage = new TextMessage(sender, message, MessageType.STANDARD);
        receivers.values().forEach(user -> user.send(ClientSender.SendAction.MESSAGE, textMessage));
    }

    /**
     * Ermittelt die Nutzer, die diese Nachricht gemäß der im räumlichen Kontext des Senders geltenden
     * Kommunikationsform empfangen sollen und leitet sie an diese weiter.
     * @param sender Kommunizierender Benutzer.
     * @param voiceData Zu versendende Nachricht.
     * @see VoiceMessage
     */
    public static void handleVoiceMessage(@NotNull User sender, final byte[] voiceData) {
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
        Map<UUID, User> receivers = communicationContext.getCommunicableUsers(sender);
        receivers.remove(sender.getUserId());
        filterIgnoredUsers(sender, receivers);

        // Versende die Sprachnachricht.
        VoiceMessage voiceMessage = new VoiceMessage(sender, voiceData);
        receivers.values().forEach(user -> user.send(ClientSender.SendAction.VOICE, voiceMessage));
    }

    /**
     * Filtert aus einer Menge von Benutzern die Benutzer heraus, die der kommunizierende Benutzer ignoriert und die
     * ihn ignorieren
     * @param sender Kommunizierender Benutzer.
     * @param users Die Menge von Benutzern, die gefiltert werden soll.
     */
    private static void filterIgnoredUsers(@NotNull User sender, @NotNull final Map<UUID, User> users) {
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
            protected void handle(User sender, String message) {
                // Extrahiere den Benutzernamen aus dem Befehl der Flüsternachricht.
                String[] usernameMessage = message.substring(1).split("\\s");
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
                } catch (UserNotFoundException e) {
                    // Informiere den kommunizierenden Benutzer darüber, dass kein Benutzer mit dem Namen existiert.
                    MessageBundle messageBundle = new MessageBundle("Es existiert kein Benutzer mit diesem Namen");
                    TextMessage infoMessage = new TextMessage(messageBundle);
                    sender.send(ClientSender.SendAction.MESSAGE, infoMessage);
                    return;
                }

                // Überprüfe, ob der Benutzer mit sich selbst kommunizieren möchte.
                if (sender.equals(receiver)) {
                    MessageBundle messageBundle = new MessageBundle("Mit sich selbst zu flüstern ist ein wenig merkwürdig.");
                    TextMessage infoMessage = new TextMessage(messageBundle);
                    sender.send(ClientSender.SendAction.MESSAGE, infoMessage);
                    return;
                }

                Area communicationContext = sender.getLocation().getArea();
                Map<UUID, User> communicableUsers = communicationContext.getCommunicableUsers(sender);

                // Überprüfe, ob die Benutzer gemäß der Kommunikationsform kommunizieren können, ob sich beide Benutzer
                // innerhalb eines Kontextes befinden, in dem einer der beiden Benutzer die Berechtigung zum Kontaktieren
                // anderer Benutzer hat und ob einer der beiden Benutzer den jeweils anderen ignoriert.
                Context commonContext = communicationContext.lastCommonAncestor(receiver.getLocation().getArea());

                if (!communicableUsers.containsKey(receiver.getUserId())
                        && !sender.hasPermission(commonContext, Permission.CONTACT_USER)
                        && !receiver.hasPermission(commonContext, Permission.CONTACT_USER)
                        || sender.isIgnoring(receiver) || receiver.isIgnoring(sender)) {
                    // Informiere den kommunizierenden Benutzer darüber, dass die Flüsterkommunikation nicht möglich ist.
                    MessageBundle messageBundle = new MessageBundle("Eine Flüsterkommunikation mit diesem Benutzer ist nicht möglich.");
                    TextMessage infoMessage = new TextMessage(messageBundle);
                    sender.send(ClientSender.SendAction.MESSAGE, infoMessage);
                    return;
                }

                // Versende die Textnachricht an den Benutzer.
                TextMessage textMessage = new TextMessage(sender, sendMessage, MessageType.WHISPER);
                sender.send(ClientSender.SendAction.MESSAGE, textMessage);
                receiver.send(ClientSender.SendAction.MESSAGE, textMessage);
            }
        },

        /**
         * Wird zur Verarbeitung einer Raumnachricht verwendet. Überprüft, ob diese Raumnachricht versendet werden
         * kann und versendet diese.
         * @see MessageType#ROOM
         */
        ROOM_MESSAGE_COMMAND("![^!].*") {
            @Override
            protected void handle(User sender, String message) {
                String sendMessage = message.substring(1);

                if (sender.getLocation() == null) {
                    throw new IllegalStateException("Users location is not available");
                }

                // Überprüfe, ob sich der kommunizierende Benutzer in einem Raum befindet, für den er die nötige Berechtigung
                // besitzt.
                Room communicationRoom = sender.getLocation().getRoom();
                if (!sender.hasPermission(communicationRoom, Permission.CONTACT_CONTEXT)) {
                    // Informiere den kommunizierenden Benutzer darüber, dass er nicht die Berechtigung besitzt, um diesen
                    // Chatbefehl zu verwenden.
                    MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung um diesen Chatbefehl zu benutzen.");
                    TextMessage infoMessage = new TextMessage(messageBundle);
                    sender.send(ClientSender.SendAction.MESSAGE, infoMessage);
                    return;
                }

                // Überprüfe, ob der kommunizierende Benutzer in dem Raum oder einem übergeordneten Kontext stummgeschaltet ist.
                if (communicationRoom.isMuted(sender)) {
                    // Informiere den kommunizierenden Benutzer darüber, dass er in dem Kontext stummgeschaltet ist.
                    MessageBundle messageBundle = new MessageBundle("Du bist in diesem Bereich stummgeschaltet.");
                    TextMessage infoMessage = new TextMessage(messageBundle);
                    sender.send(ClientSender.SendAction.MESSAGE, infoMessage);
                    return;
                }

                // Ermittle die empfangsberechtigten Benutzer.
                Map<UUID, User> receivers = communicationRoom.getUsers();
                filterIgnoredUsers(sender, receivers);

                // Versende die Textnachricht.
                TextMessage textMessage = new TextMessage(sender, sendMessage, MessageType.ROOM);
                receivers.values().forEach(user -> user.send(ClientSender.SendAction.MESSAGE, textMessage));
            }
        },

        /**
         * Wird zur Verarbeitung einer Weltnachricht verwendet. Überprüft, ob diese Weltnachricht versendet werden
         * kann und versendet diese.
         * @see MessageType#WORLD
         */
        WORLD_MESSAGE_COMMAND("!!.*"){
            @Override
            protected void handle(User sender, String message) {
                String sendMessage = message.substring(2);

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
                    MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung um diesen Chatbefehl zu benutzen.");
                    TextMessage infoMessage = new TextMessage(messageBundle);
                    sender.send(ClientSender.SendAction.MESSAGE, infoMessage);
                    return;
                }

                // Überprüfe, ob der kommunizierende Benutzer in dem Raum oder einem übergeordneten Kontext stummgeschaltet ist.
                if (communicationWorld.isMuted(sender)) {
                    // Informiere den kommunizierenden Benutzer darüber, dass er in dem Kontext stummgeschaltet ist.
                    MessageBundle messageBundle = new MessageBundle("Du bist in diesem Bereich stummgeschaltet.");
                    TextMessage infoMessage = new TextMessage(messageBundle);
                    sender.send(ClientSender.SendAction.MESSAGE, infoMessage);
                    return;
                }

                // Ermittle die empfangsberechtigten Benutzer.
                Map<UUID, User> receivers = communicationWorld.getUsers();
                filterIgnoredUsers(sender, receivers);

                // Versende die Textnachricht.
                TextMessage textMessage = new TextMessage(sender, sendMessage, MessageType.WORLD);
                receivers.values().forEach(user -> user.send(ClientSender.SendAction.MESSAGE, textMessage));
            }
        };

        /** Ein regulärer Ausdruck eines spezifischen Chatbefehls. */
        private final String commandPattern;

        /**
         * Verarbeitet die Nachricht gemäß dem Chatbefehl.
         * @param sender Sender der Nachricht.
         * @param message Erhaltene Nachricht.
         */
        protected abstract void handle(User sender, String message);

        /**
         * Erzeugt eine Instanz eines Chatbefehls.
         * @param commandPattern Pattern der Nachricht zur Verwendung dieses Chatbefehls.
         */
        ChatCommand(String commandPattern) {
            this.commandPattern = commandPattern;
        }
    }
}
