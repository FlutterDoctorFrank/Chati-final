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

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Eine Klasse, deren Objekte von einem Benutzer gesendete Nachrichten zumWeitersenden
 * über das Netzwerk verarbeitet.
 */
public class CommunicationHandler {

    /** Ein reguläre Ausdruck für das Verwenden eines Chatbefehls in einer Nachricht.*/
    private static final String CHAT_COMMAND = "\\\\\\s.+";

    /** Ein regulärer Ausdruck für das Verwenden einer Flüsternachricht. */
    private static final String WHISPER_MESSAGE = "\\\\\\w\\s.+";

    /** Ein regulärer Ausdruck für das Verwenden einer Raumnachricht. */
    private static final String ROOM_MESSAGE = "!\\s.+";

    /** Ein regulärer Ausdruck für das Verwenden einer Weltnachricht. */
    private static final String WORLD_MESSAGE = "!!\\s.+";

    /** Der kommunizierende Benutzer dieser Instanz. */
    private final User communicator;

    /**
     * Erzeugt eine neue Instanz des CommunicationHandler.
     * @param user Der kommunizierende Benutzer.
     */
    public CommunicationHandler(User user) {
        this.communicator = user;
    }

    /**
     * Ermittelt den Nachrichtentyp der Nachricht durch ein am Anfang eingegebenes Muster, sowie die Nutzer, die diese
     * Nachricht empfangen sollen und leitet sie an diese weiter. Existiert kein Nachrichtentyp für das eingegebene
     * Muster, oder hat der Anwender nicht die Berechtigung diesen zu benutzen, wird er mit einer Textnachricht
     * darüber informiert.
     * @param message Zu versendende Nachricht.
     * @see TextMessage
     * @see MessageType
     */
    public void handleTextMessage(String message) {
        // Überprüfe, ob ein Chatbefehl verwendet wurde.
        if (message.matches(CHAT_COMMAND)) {
            // Das erste Attribut dieses Felds entspricht dem Chatbefehl, das zweite der restlichen Nachricht.
            String[] commandMessage = message.split("\\s");
            switch (commandMessage[0]) {
                case WHISPER_MESSAGE:
                    // Extrahiere den Benutzernamen aus dem Befehl der Flüsternachricht.
                    String username = Pattern.compile("\\w").matcher(commandMessage[0]).group(1);
                    // Verarbeite die Flüsternachricht.
                    handleWhisperMessage(username, commandMessage[1]);
                    break;
                case ROOM_MESSAGE:
                    // Verarbeite die Raumnachricht.
                    handleRoomMessage(commandMessage[1]);
                    break;
                case WORLD_MESSAGE:
                    // Verarbeite die Weltnachricht.
                    handleWorldMessage(commandMessage[1]);
                    break;
                default:
                    // Informiere den kommunizierenden Benutzer darüber, dass er versucht hat, einen nicht existierenden
                    // Chatbefehl zu verwenden.
                    MessageBundle messageBundle = new MessageBundle("Dieser Chatbefehl existiert nicht.");
                    TextMessage infoMessage = new TextMessage(messageBundle);
                    communicator.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            }
        } else {
            // Verarbeite eine normale Chatnachricht, falls kein Chatbefehl verwendet wurde.
            handleStandardMessage(message);
        }
    }

    /**
     * Ermittelt die Nutzer, die diese Nachricht gemäß der im räumlichen Kontext des Senders geltenden
     * Kommunikationsform empfangen sollen und leitet sie an diese weiter.
     * @param voicedata Zu versendende Nachricht.
     * @see VoiceMessage
     */
    public void handleVoiceMessage(byte[] voicedata) {
        // Überprüfe, ob in dem Bereich des Benutzers Sprachnachrichten versendet werden können, oder der sendende
        // Benutzer in dem Kontext stummgeschaltet ist.
        Area communicationContext = communicator.getLocation().getArea();
        if (!communicationContext.canCommunicateWith(CommunicationMedium.VOICE)
            || communicationContext.isMuted(communicator)) {
            return;
        }
        // Ermittle die empfangsberechtigten Benutzer gemäß der Kommunikationsform, ohne den sendenden Benutzer.
        Map<UUID, User> communicableUsers = communicationContext.getCommunicableUsers(communicator);
        Map<UUID, User> receivers = filterIgnoredUsers(communicator, communicableUsers);
        receivers.remove(communicator.getUserId());
        // Versende die Sprachnachricht.
        VoiceMessage voiceMessage = new VoiceMessage(communicator, voicedata);
        receivers.values().forEach(user -> {
            user.getClientSender().send(ClientSender.SendAction.VOICE, voiceMessage);
        });
    }

    /**
     * Ermittelt die Nutzer, die diese Nachricht gemäß der im räumlichen Kontext des Senders geltenden
     * Kommunikationsform empfangen sollen und leitet sie an diese weiter.
     * @param message Zu versendende Nachricht.
     * @see MessageType#STANDARD
     */
    private void handleStandardMessage(String message) {
        // Überprüfe, ob in dem Bereich des Benutzers Textnachrichten versendet werden können.
        Area communicationContext = communicator.getLocation().getArea();
        if (!communicationContext.canCommunicateWith(CommunicationMedium.TEXT)) {
            MessageBundle messageBundle = new MessageBundle("In diesem Bereich ist keine Kommunikation in Schriftform möglich.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der sendende Benutzer in dem Kontext stummgeschaltet ist.
        if (communicationContext.isMuted(communicator)) {
            // Informiere den kommunizierenden Benutzer darüber, dass er in dem Kontext stummgeschaltet ist.
            MessageBundle messageBundle = new MessageBundle("Du bist in diesem Bereich stummgeschaltet.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Ermittle die empfangsberechtigten Benutzer gemäß der Kommunikationsform, einschließlich dem sendenden
        // Benutzer.
        Map<UUID, User> communicableUsers = communicationContext.getCommunicableUsers(communicator);
        Map<UUID, User> receivers = filterIgnoredUsers(communicator, communicableUsers);
        // Versende die Textnachricht.
        TextMessage textMessage = new TextMessage(communicator, message, MessageType.STANDARD);
        receivers.values().forEach(user -> {
            user.getClientSender().send(ClientSender.SendAction.MESSAGE, textMessage);
        });
    }

    /**
     * Wird zur Verarbeitung einer Flüsternachricht verwendet. Überprüft, ob diese Flüsternachricht versendet werden
     * kann und versendet diese.
     * @param username Name des Benutzers, der die Nachricht erhalten soll.
     * @param message Zu versendende Nachricht.
     * @see MessageType#WHISPER
     */
    private void handleWhisperMessage(String username, String message) {
        // Überprüfe, ob ein Benutzer mit dem angegebenen Benutzernamen existiert.
        User receiver;
        try {
            receiver = UserAccountManager.getInstance().getUser(username);
        } catch (UserNotFoundException e) {
            // Informiere den kommunizierenden Benutzer darüber, dass kein Benutzer mit dem Namen existiert.
            MessageBundle messageBundle = new MessageBundle("Es existiert kein Benutzer mit diesem Namen");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        Area communicationContext = communicator.getLocation().getArea();
        Map<UUID, User> communicableUsers = communicationContext.getCommunicableUsers(communicator);
        // Überprüfe, ob die Benutzer gemäß der Kommunikationsform kommunizieren können, ob sich beide Benutzer
        // innerhalb eines Kontextes befinden in dem einer der beiden Benutzer die Berechtigung zum Kontaktieren
        // anderer Benutzer hat und ob einer der beiden Benutzer den jeweils anderen ignoriert.
        Context commonContext = communicationContext.lastCommonAncestor(receiver.getLocation().getArea());
        if (!communicableUsers.containsKey(receiver.getUserId())
                && !communicator.hasPermission(commonContext, Permission.CONTACT_USER)
                && !receiver.hasPermission(commonContext, Permission.CONTACT_USER)
                || communicator.isIgnoring(receiver) || receiver.isIgnoring(communicator)) {
            // Informiere den kommunizierenden Benutzer darüber, dass die Flüsterkommunikation nicht möglich ist.
            MessageBundle messageBundle = new MessageBundle("Eine Flüsterkommunikation mit diesem Benutzer ist nicht möglich.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Versende die Textnachricht an den Benutzer.
        TextMessage textMessage = new TextMessage(communicator, message, MessageType.WHISPER);
        communicator.getClientSender().send(ClientSender.SendAction.MESSAGE, textMessage);
        receiver.getClientSender().send(ClientSender.SendAction.MESSAGE, textMessage);
    }

    /**
     * Wird zur Verarbeitung einer Raumnachricht verwendet. Überprüft, ob diese Raumnachricht versendet werden
     * kann und versendet diese.
     * @param message Zu versendende Nachricht.
     * @see MessageType#ROOM
     */
    private void handleRoomMessage(String message) {
        // Überprüfe, ob sich der kommunizierende Benutzer in einem Raum befindet, für den er die nötige Berechtigung
        // besitzt.
        Room communicationRoom = communicator.getLocation().getRoom();
        if (!communicator.hasPermission(communicationRoom, Permission.CONTACT_CONTEXT)) {
            // Informiere den kommunizierenden Benutzer darüber, dass er nicht die Berechtigung besitzt, um diesen
            // Chatbefehl zu verwenden.
            MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung um diesen Chatbefehl zu benutzen.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der kommunizierende Benutzer in dem Raum oder einem übergeordneten Kontext stummgeschaltet ist.
        if (communicationRoom.isMuted(communicator)) {
            // Informiere den kommunizierenden Benutzer darüber, dass er in dem Kontext stummgeschaltet ist.
            MessageBundle messageBundle = new MessageBundle("Du bist in diesem Bereich stummgeschaltet.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Ermittle die empfangsberechtigten Benutzer.
        Map<UUID, User> communicableUsers = communicationRoom.getUsers();
        Map<UUID, User> receivers = filterIgnoredUsers(communicator, communicableUsers);
        // Versende die Textnachricht.
        TextMessage textMessage = new TextMessage(communicator, message, MessageType.ROOM);
        receivers.values().forEach(user -> {
            user.getClientSender().send(ClientSender.SendAction.MESSAGE, textMessage);
        });
    }

    /**
     * Wird zur Verarbeitung einer Weltnachricht verwendet. Überprüft, ob diese Weltnachricht versendet werden
     * kann und versendet diese.
     * @param message Zu versendende Nachricht.
     * @see MessageType#WORLD
     */
    private void handleWorldMessage(String message) {
        // Überprüfe, ob sich der kommunizierende Benutzer in einer Welt befindet, für die er die nötige Berechtigung
        // besitzt.
        World communicationWorld = communicator.getWorld();
        if (!communicator.hasPermission(communicationWorld, Permission.CONTACT_CONTEXT)) {
            // Informiere den kommunizierenden Benutzer darüber, dass er nicht die Berechtigung besitzt, um diesen
            // Chatbefehl zu verwenden.
            MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung um diesen Chatbefehl zu benutzen.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Überprüfe, ob der kommunizierende Benutzer in dem Raum oder einem übergeordneten Kontext stummgeschaltet ist.
        if (communicationWorld.isMuted(communicator)) {
            // Informiere den kommunizierenden Benutzer darüber, dass er in dem Kontext stummgeschaltet ist.
            MessageBundle messageBundle = new MessageBundle("Du bist in diesem Bereich stummgeschaltet.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.getClientSender().send(ClientSender.SendAction.MESSAGE, infoMessage);
            return;
        }
        // Ermittle die empfangsberechtigten Benutzer.
        Map<UUID, User> communicableUsers = communicationWorld.getUsers();
        Map<UUID, User> receivers = filterIgnoredUsers(communicator, communicableUsers);
        // Versende die Textnachricht.
        TextMessage textMessage = new TextMessage(communicator, message, MessageType.WORLD);
        receivers.values().forEach(user -> {
            user.getClientSender().send(ClientSender.SendAction.MESSAGE, textMessage);
        });
    }

    /**
     * Filtert aus einer Menge von Benutzern die Benutzer heraus, die der kommunizierende Benutzer ignoriert und die
     * ihn ignorieren
     * @param communicator Kommunizierende Benutzer.
     * @param communicableUsers Menge von Benutzern, die gefiltert werden soll.
     * @return Gefilterte Menge von Benutzern.
     */
    private Map<UUID, User> filterIgnoredUsers(User communicator, Map<UUID, User> communicableUsers) {
        return communicableUsers.values().stream()
                .filter(communicableUser -> !communicator.isIgnoring(communicableUser)
                && !communicableUser.isIgnoring(communicator))
                .collect(Collectors.toMap(User::getUserId, Function.identity()));
    }
}