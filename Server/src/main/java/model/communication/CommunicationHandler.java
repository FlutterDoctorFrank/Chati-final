package model.communication;

import controller.network.ClientSender.SendAction;
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
import java.util.regex.Pattern;

/**
 * Eine Klasse, deren Objekte von einem Benutzer gesendete Nachrichten zumWeitersenden
 * über das Netzwerk verarbeitet.
 */
public class CommunicationHandler {

    /** Ein regulärer Ausdruck zur Erkennung eines Chatbefehls in einer Nachricht */
    private static final Pattern COMMAND = Pattern.compile("^((/\\w+)|!|!!)\\s.*");

    /** Ein regulärer Ausdruck für das Verwenden einer Raumnachricht. */
    private static final String ROOM_MESSAGE = "!";

    /** Ein regulärer Ausdruck für das Verwenden einer Weltnachricht. */
    private static final String WORLD_MESSAGE = "!!";

    /** Der kommunizierende Benutzer dieser Instanz. */
    private final User communicator;

    /**
     * Erzeugt eine neue Instanz des CommunicationHandler.
     * @param user Der kommunizierende Benutzer.
     */
    public CommunicationHandler(@NotNull final User user) {
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
    public void handleTextMessage(@NotNull final String message) {
        /*
         * Ein Befehl beginnt entweder mit '!', '!!' oder mit '/' und einem nachfolgenden Leerzeichen, wobei im
         * Fall '/' weitere Wortzeichen zwischen dem ersten Zeichen und dem Leerzeichen erwartet werden. Diese
         * Wortzeichen werden als Benutzername für die Flüster-Kommunikation interpretiert.
         * Beginnt die Nachricht nicht mit diesen Zeichen, so wird die Nachricht als Chatnachricht interpretiert.
         */
        if (COMMAND.matcher(message).matches()) {
            final String[] command = message.split("\\s", 2);

            switch (command[0]) {
                case ROOM_MESSAGE:
                    handleRoomMessage(command[1]);
                    break;

                case WORLD_MESSAGE:
                    handleWorldMessage(command[1]);
                    break;

                default:
                    handleWhisperMessage(command[0].substring(1), command[1]);
                    break;
            }
        } else {
            handleStandardMessage(message);
        }
    }

    /**
     * Ermittelt die Nutzer, die diese Nachricht gemäß der im räumlichen Kontext des Senders geltenden
     * Kommunikationsform empfangen sollen und leitet sie an diese weiter.
     * @param voiceData Zu versendende Nachricht.
     * @see VoiceMessage
     */
    public void handleVoiceMessage(final byte[] voiceData) {
        if (communicator.getLocation() == null) {
            throw new IllegalStateException("Communicators location is not available.");
        }

        /*
         * Überprüfung, ob in dem Bereich des Benutzers Sprachnachrichten versendet werden können oder der sendende
         * Benutzer in dem Kontext stumm geschaltet ist.
         */
        Area communicationContext = communicator.getLocation().getArea();
        if (!communicationContext.canCommunicateWith(CommunicationMedium.VOICE)
            || communicationContext.isMuted(communicator)) {
            return;
        }

        // Ermittle die empfangsberechtigten Benutzer gemäß der Kommunikationsform, ohne den sendenden Benutzer.
        Map<UUID, User> receivers = communicationContext.getCommunicableUsers(communicator);
        receivers.remove(communicator.getUserId());
        filterIgnoredUsers(receivers);

        // Versende die Sprachnachricht.
        VoiceMessage voiceMessage = new VoiceMessage(communicator, voiceData);
        receivers.values().forEach(user -> user.send(SendAction.VOICE, voiceMessage));
    }

    /**
     * Ermittelt die Nutzer, die diese Nachricht gemäß der im räumlichen Kontext des Senders geltenden
     * Kommunikationsform empfangen sollen und leitet sie an diese weiter.
     * @param message Zu versendende Nachricht.
     * @see MessageType#STANDARD
     */
    private void handleStandardMessage(@NotNull final String message) {
        if (communicator.getLocation() == null) {
            throw new IllegalStateException("Communicators location is not available");
        }

        // Überprüfe, ob in dem Bereich des Benutzers Textnachrichten versendet werden können.
        Area communicationContext = communicator.getLocation().getArea();
        if (!communicationContext.canCommunicateWith(CommunicationMedium.TEXT)) {
            MessageBundle messageBundle = new MessageBundle("In diesem Bereich ist keine Kommunikation in Schriftform möglich.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        // Überprüfe, ob der sendende Benutzer in dem Kontext stumm geschaltet ist.
        if (communicationContext.isMuted(communicator)) {
            // Informiere den kommunizierenden Benutzer darüber, dass er in dem Kontext stumm geschaltet ist.
            MessageBundle messageBundle = new MessageBundle("Du bist in diesem Bereich stumm geschaltet.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        // Ermittle die empfangsberechtigten Benutzer gemäß der Kommunikationsform, einschließlich dem sendenden
        // Benutzer.
        Map<UUID, User> receivers = communicationContext.getCommunicableUsers(communicator);
        filterIgnoredUsers(receivers);

        // Versende die Textnachricht.
        TextMessage textMessage = new TextMessage(communicator, message, MessageType.STANDARD);
        receivers.values().forEach(user -> user.send(SendAction.MESSAGE, textMessage));
    }

    /**
     * Wird zur Verarbeitung einer Flüsternachricht verwendet. Überprüft, ob diese Flüsternachricht versendet werden
     * kann und versendet diese.
     * @param username Name des Benutzers, der die Nachricht erhalten soll.
     * @param message Zu versendende Nachricht.
     * @see MessageType#WHISPER
     */
    private void handleWhisperMessage(@NotNull final String username, @NotNull final String message) {
        if (communicator.getLocation() == null) {
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
            communicator.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        Area communicationContext = communicator.getLocation().getArea();
        Map<UUID, User> communicableUsers = communicationContext.getCommunicableUsers(communicator);

        // Überprüfe, ob die Benutzer gemäß der Kommunikationsform kommunizieren können, ob sich beide Benutzer
        // innerhalb eines Kontextes befinden, in dem einer der beiden Benutzer die Berechtigung zum Kontaktieren
        // anderer Benutzer hat und ob einer der beiden Benutzer den jeweils anderen ignoriert.
        Context commonContext = communicationContext.lastCommonAncestor(receiver.getLocation().getArea());

        if (commonContext == null || (!communicableUsers.containsKey(receiver.getUserId())
                && !communicator.hasPermission(commonContext, Permission.CONTACT_USER)
                && !receiver.hasPermission(commonContext, Permission.CONTACT_USER)
                || communicator.isIgnoring(receiver) || receiver.isIgnoring(communicator))) {
            // Informiere den kommunizierenden Benutzer darüber, dass die Flüster-Kommunikation nicht möglich ist.
            MessageBundle messageBundle = new MessageBundle("Eine Flüster-Kommunikation mit diesem Benutzer ist nicht möglich.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        // Versende die Textnachricht an den Benutzer.
        TextMessage textMessage = new TextMessage(communicator, message, MessageType.WHISPER);
        communicator.send(SendAction.MESSAGE, textMessage);
        receiver.send(SendAction.MESSAGE, textMessage);
    }

    /**
     * Wird zur Verarbeitung einer Raumnachricht verwendet. Überprüft, ob diese Raumnachricht versendet werden
     * kann und versendet diese.
     * @param message Zu versendende Nachricht.
     * @see MessageType#ROOM
     */
    private void handleRoomMessage(@NotNull final String message) {
        if (communicator.getLocation() == null) {
            throw new IllegalStateException("Users location is not available");
        }

        // Überprüfe, ob sich der kommunizierende Benutzer in einem Raum befindet, für den er die nötige Berechtigung
        // besitzt.
        Room communicationRoom = communicator.getLocation().getRoom();
        if (!communicator.hasPermission(communicationRoom, Permission.CONTACT_CONTEXT)) {
            // Informiere den kommunizierenden Benutzer darüber, dass er nicht die Berechtigung besitzt, um diesen
            // Chatbefehl zu verwenden.
            MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung um diesen Chatbefehl zu benutzen.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        // Überprüfe, ob der kommunizierende Benutzer in dem Raum oder einem übergeordneten Kontext stummgeschaltet ist.
        if (communicationRoom.isMuted(communicator)) {
            // Informiere den kommunizierenden Benutzer darüber, dass er in dem Kontext stummgeschaltet ist.
            MessageBundle messageBundle = new MessageBundle("Du bist in diesem Bereich stummgeschaltet.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        // Ermittle die empfangsberechtigten Benutzer.
        Map<UUID, User> receivers = communicationRoom.getCommunicableUsers(communicator);
        filterIgnoredUsers(receivers);

        // Versende die Textnachricht.
        TextMessage textMessage = new TextMessage(communicator, message, MessageType.ROOM);
        receivers.values().forEach(user -> user.send(SendAction.MESSAGE, textMessage));
    }

    /**
     * Wird zur Verarbeitung einer Weltnachricht verwendet. Überprüft, ob diese Weltnachricht versendet werden
     * kann und versendet diese.
     * @param message Zu versendende Nachricht.
     * @see MessageType#WORLD
     */
    private void handleWorldMessage(@NotNull final String message) {
        if (communicator.getLocation() == null) {
            throw new IllegalStateException("Users location is not available");
        }
        // Überprüfe, ob sich der kommunizierende Benutzer in einer Welt befindet, für die er die nötige Berechtigung
        // besitzt.
        World communicationWorld = communicator.getWorld();

        if (communicationWorld == null) {
            throw new IllegalStateException("Users world is not available");
        }

        if (!communicator.hasPermission(communicationWorld, Permission.CONTACT_CONTEXT)) {
            // Informiere den kommunizierenden Benutzer darüber, dass er nicht die Berechtigung besitzt, um diesen
            // Chatbefehl zu verwenden.
            MessageBundle messageBundle = new MessageBundle("Du hast nicht die nötige Berechtigung um diesen Chatbefehl zu benutzen.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        // Überprüfe, ob der kommunizierende Benutzer in dem Raum oder einem übergeordneten Kontext stummgeschaltet ist.
        if (communicationWorld.isMuted(communicator)) {
            // Informiere den kommunizierenden Benutzer darüber, dass er in dem Kontext stummgeschaltet ist.
            MessageBundle messageBundle = new MessageBundle("Du bist in diesem Bereich stummgeschaltet.");
            TextMessage infoMessage = new TextMessage(messageBundle);
            communicator.send(SendAction.MESSAGE, infoMessage);
            return;
        }

        // Ermittle die empfangsberechtigten Benutzer.
        Map<UUID, User> receivers = communicationWorld.getWorldUsers();
        filterIgnoredUsers(receivers);

        // Versende die Textnachricht.
        TextMessage textMessage = new TextMessage(communicator, message, MessageType.WORLD);
        receivers.values().forEach(user -> user.send(SendAction.MESSAGE, textMessage));
    }

    /**
     * Filtert aus einer Menge von Benutzern die Benutzer heraus, die der kommunizierende Benutzer ignoriert und die
     * ihn ignorieren
     * @param users die Menge von Benutzern, die gefiltert werden soll.
     */
    private void filterIgnoredUsers(@NotNull final Map<UUID, User> users) {
        users.entrySet().removeIf(user -> this.communicator.isIgnoring(user.getValue()) || user.getValue().isIgnoring(this.communicator));
    }
}